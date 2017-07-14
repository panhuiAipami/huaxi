package com.tools.commonlibs.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.tools.commonlibs.tools.LogUtils;

import android.os.Handler;
import android.os.Looper;

/**
 * 异步任务抽象类
 * 
 * 性能是new Thread().start()的5到15倍
 */
@SuppressWarnings("unchecked")
public abstract class EasyTask<Caller, Params, Progress, Result> implements Task<Caller, Params, Progress, Result> {
	private static final BlockingQueue<Runnable> WORK_QUEUE = new SynchronousQueue<Runnable>();// 同步队列（避免出现线程池锁）
	private static final int CORE_POOL_SIZE = 5;// 核心线程（池中所保存的线程数，包括空闲线程）
	private static final int MAXIMUM_POOL_SIZE = Integer.MAX_VALUE;// 最大线程（无界maximumPoolSizes避免拒绝新提交的任务，但消耗资源
																	// ）
	private static final int KEEP_ALIVE_TIME = 30;// 线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间。
	private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {// 线程工厂

		private final AtomicInteger mCount = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "EasyTask # " + mCount.getAndIncrement());
		}
	};

	private static final ExecutorService WORKERS = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
			WORK_QUEUE, THREAD_FACTORY) {

		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);

			if (t == null && r instanceof Future<?>) {
				try {
					Future<?> future = (Future<?>) r;
					if (future.isDone())
						future.get();
				} catch (CancellationException ce) {
					t = ce;
				} catch (ExecutionException ee) {
					t = ee.getCause();
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt(); // ignore/reset
				}
			}

			if (t != null)
				LogUtils.error("EasyTask|doInBackground|uncatch|" + t.getMessage(), t);
		}

	};// 线程工人

	private Handler handler;// UI处理

	protected Caller caller;// 当前异步任务的调用者

	private volatile Status mStatus = Status.PENDING;

	protected Boolean isNeedBefore = true;

	protected Boolean isNeedAfter = true;

	public enum Status {
		/**
		 * Indicates that the task has not been executed yet.
		 */
		PENDING,
		/**
		 * Indicates that the task is running.
		 */
		RUNNING,
		/**
		 * Indicates that {@link MyTask#onPostExecute} has finished.
		 */
		FINISHED,
	}

	private Result result;

	public EasyTask(Caller caller) {
		this.caller = caller;
		// 使用主线程looper
		handler = new Handler(Looper.getMainLooper());
	}

	@Override
	public void execute(final Params... params) {

		WORKERS.execute(new Runnable() {

			@Override
			public void run() {

				try {

					/**
					 * 1. 初始化
					 */
					if (isNeedBefore) {

						handler.post(new Runnable() {

							@Override
							public void run() {

								try {

									onPreExecute();

								} catch (Throwable e) {
									LogUtils.error("EasyTask|onPreExecute|" + e.getMessage(), e);
								} finally {
									mStatus = Status.RUNNING;
								}
							}
						});

					} else
						mStatus = Status.RUNNING;

					/**
					 * 2. 执行异步操作
					 */
					result = doInBackground(params);

				} catch (Throwable e) {
					LogUtils.error("EasyTask|doInBackground|" + e.getMessage(), e);
				} finally {

					/**
					 * 3. 异步操作后执行UI更新(成功或失败)
					 */
					if (isNeedAfter) {

						handler.post(new Runnable() {

							@Override
							public void run() {

								try {

									onPostExecute(result);

								} catch (Throwable e) {
									LogUtils.error("EasyTask|onPostExecute|" + e.getMessage(), e);
								} finally {
									mStatus = Status.FINISHED;
								}

							}
						});

					} else
						mStatus = Status.FINISHED;

				}

			}
		});

	}

	@Override
	public Future<Result> submit(final Params... params) {

		Future<Result> future = WORKERS.submit(new Callable<Result>() {

			@Override
			public Result call() throws Exception {

				try {

					/**
					 * 1. 初始化
					 */
					if (isNeedBefore) {

						handler.post(new Runnable() {

							@Override
							public void run() {

								try {

									onPreExecute();

								} catch (Throwable e) {
									LogUtils.error("EasyTask|onPreExecute|" + e.getMessage(), e);
								} finally {
									mStatus = Status.RUNNING;
								}
							}
						});

					} else
						mStatus = Status.RUNNING;

					/**
					 * 2. 执行异步操作
					 */
					result = doInBackground(params);

				} catch (Throwable e) {
					LogUtils.error("EasyTask|doInBackground|" + e.getMessage(), e);
				} finally {

					/**
					 * 3. 异步操作后执行UI更新(成功或失败)
					 */
					if (isNeedAfter) {

						handler.post(new Runnable() {

							@Override
							public void run() {

								try {

									onPostExecute(result);

								} catch (Throwable e) {
									LogUtils.error("EasyTask|onPostExecute|" + e.getMessage(), e);
								} finally {
									mStatus = Status.FINISHED;
								}

							}
						});

					} else
						mStatus = Status.FINISHED;

				}

				return result;
			}
		});

		return future;

	}

	@Override
	public void publishProgress(final Progress... values) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				onProgressUpdate(values);
			}
		});
	}

	public static void addTask(final Runnable runnable) {
		WORKERS.execute(new Runnable() {

			@Override
			public void run() {
				try {
					runnable.run();
				} catch (Throwable e) {
					LogUtils.error(e.getMessage(), e);
				}
			}
		});
	}

	/**
	 * Returns the current status of this task.
	 * 
	 * @return The current status.
	 */
	public final Status getStatus() {
		return mStatus;
	}

	public final Handler getHandler() {
		return handler;
	}

	public static final ExecutorService getWorkers() {

		return WORKERS;

	}

	@Override
	public void onPreExecute() {
	}

	@Override
	public void onPostExecute(Result result) {
	}

	@Override
	public void onProgressUpdate(Progress... values) {
	}

	@Override
	public abstract Result doInBackground(Params... params);

}
