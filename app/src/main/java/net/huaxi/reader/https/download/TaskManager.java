package net.huaxi.reader.https.download;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import net.huaxi.reader.https.download.listener.ITaskStateChangeListener;


public class TaskManager {

    Map<String, Task> allTasks = null;  //缓存内容中的任务;
    Map<Task, TaskWorker> startedTasksMap;  //正在下载的任务集合;
    LinkedBlockingQueue<Task> preparedTaskQueue;  //等待中的任务列表;
    boolean disconnected;  //数据连接是否断开;
    volatile int concurrentTasks = 4; //最大线程数;
    protected ExecutorService executor;  //线程池;
    private static TaskManager singleton;
    private TaskDispatcher taskDispatcher;
    private Thread taskDispatcherThread;

    public static TaskManager getInstance() {
        if (singleton == null) {
            synchronized (TaskManager.class) {
                if (singleton == null) {
                    singleton = new TaskManager();
                }
            }
        }
        return singleton;
    }

    private TaskManager() {
        init();
    }

    private void init() {
        allTasks = new HashMap<String, Task>();
        startedTasksMap = Collections.synchronizedMap(new HashMap<Task, TaskWorker>());
        preparedTaskQueue = new LinkedBlockingQueue<Task>();

        executor = Executors.newFixedThreadPool(concurrentTasks);
        taskDispatcher = new TaskDispatcher(this);
        taskDispatcherThread = new Thread(taskDispatcher);
        taskDispatcherThread.start();
    }

    public int getConcurrentTasks() {
        return concurrentTasks;
    }


    public boolean isDisconnected() {
        return disconnected;
    }


    /**
     * 启动下载任务
     *
     * @throws InterruptedException
     */
    public synchronized int startTask(Task task) throws InterruptedException {
        int code = TaskErrorEnum.TASK_SUCCESS.getCode();
        if (allTasks.containsKey(task.getUrl())) {
            Task exist = allTasks.get(task.getUrl());
            exist.setListener(task.getListener());
            code = TaskErrorEnum.TASK_HAS_ADDED.getCode();
        } else {
            preparedTaskQueue.put(task);
            synchronized (allTasks) {
                allTasks.put(task.getUrl(), task);
            }
        }
        return code;
    }

    /**
     * 停止下载任务
     *
     * @param task
     * @return
     */
    public int stopTask(Task task) {
        int code = TaskErrorEnum.TASK_SUCCESS.getCode();
        synchronized (preparedTaskQueue) {
            if (preparedTaskQueue.contains(task)) {
                preparedTaskQueue.remove(task);
            }
        }
        synchronized (startedTasksMap) {
            if (startedTasksMap.containsKey(task)) {
                TaskWorker worker = startedTasksMap.remove(task);
                if (worker != null) {
                    worker.setCanceled(true);
                }
            }
        }
        allTasks.remove(task.getUrl());
        return code;
    }

    /**
     * 注册任务的监听回调接口
     *
     * @param task
     * @param listener
     */
    public void registerStateChangeListener(Task task, ITaskStateChangeListener listener) {
        TaskWorker worker = getTaskWorker(task);
        worker.registerStateChangeListener(listener);
    }

    /**
     * 查找任务
     *
     * @param url
     * @return
     */
    public Task findTask(String url) {
        Task task = allTasks.get(url);
        return task;
    }

    /**
     * 设置线程池最大数;
     *
     * @param max
     */
    public void setConcurrentTasks(int max) {
        concurrentTasks = max;
        if (executor == null) {
            return;
        }

        if (executor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor pool = (ThreadPoolExecutor) executor;
            if (pool.getCorePoolSize() > max) {
                pool.setCorePoolSize(concurrentTasks);
                pool.setMaximumPoolSize(concurrentTasks);
            } else {
                pool.setMaximumPoolSize(concurrentTasks);
                pool.setCorePoolSize(concurrentTasks);
            }
        }

        // After reset concurrentTasks, dispatcher shall re-check whether totake task
        if (taskDispatcher != null && !disconnected) {
            synchronized (taskDispatcher) {
                taskDispatcher.notifyAll();
            }
        }
    }

    public Task getTask() throws InterruptedException {
        Task task = preparedTaskQueue.take();
        return task;
    }

    public TaskWorker getTaskWorker(Task task) {
        TaskWorker worker = null;
        if (startedTasksMap != null) {
            worker = startedTasksMap.get(task);
        }
        return worker;
    }

    /**
     * 是否有空闲线程.
     *
     * @return
     */
    public boolean hasIdleWorks() {
        boolean result = false;
        synchronized (startedTasksMap) {
            result = (concurrentTasks - startedTasksMap.size() > 0);
        }
        return result;
    }

    /**
     * 是否有空闲任务;
     *
     * @return
     */
    public boolean hasIdleTask() {
        boolean result = false;
        synchronized (preparedTaskQueue) {
            result = preparedTaskQueue.size() > 0;
        }
        return result;
    }

    protected synchronized void doTask(Task task) {
        synchronized (startedTasksMap) {
            TaskWorker worker = new TaskWorker(task, singleton, taskDispatcherThread);
            startedTasksMap.put(task, worker);
            executor.submit(worker);
        }
    }

    /**
     * 删除一条任务.
     *
     * @param task
     * @return
     */
    public int removeTask(Task task) {
        if (task == null) {
            return -1;
        }
        int code = TaskErrorEnum.TASK_SUCCESS.getCode();
        synchronized (preparedTaskQueue) {
            if (preparedTaskQueue.contains(task)) {
                preparedTaskQueue.remove(task);
            }
        }
        synchronized (startedTasksMap) {
            TaskWorker worker = startedTasksMap.remove(task);
            if (worker != null) {
                worker.setCanceled(true);
            }
            worker = null;
        }
        synchronized (allTasks) {
            allTasks.remove(task.getUrl());
        }
        return code;
    }

    public void clearAll() {
        Set<String> keySet = allTasks.keySet();
        for (String s : keySet) {
            Task task = allTasks.get(s);
            removeTask(task);
        }
    }

}
