package com.tools.commonlibs.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

	/**
	 * 递归删除目录下的所有文件及子目录下所有文件
	 * @param dir 将要删除的文件目录
	 * @return boolean Returns "true" if all deletions were successful.
	 *                 If a deletion fails, the method stops attempting to
	 *                 delete and returns "false".
	 */
	public static boolean deleteDir(File file) {
		return deleteDir(file, null);
	}

	/**
	 * 递归删除目录下的所有文件及子目录下所有文件
	 * @param dir 将要删除的文件目录
	 * @return boolean Returns "true" if all deletions were successful.
	 *                 If a deletion fails, the method stops attempting to
	 *                 delete and returns "false".
	 */
	public static boolean deleteDir(File file, FilenameFilter filter) {
		if (!file.exists())
			return false;

		if (file.isDirectory()) {
			String[] children = file.list(filter);
			//递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(file, children[i]), filter);
				if (!success)
					return false;
			}
		}
		// 目录此时为空，可以删除
		return file.delete();
	}

    /**
     * 删除文件
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return false;
        }
        return file.delete();
    }

	/**
	 * 递归删除目录下的所有文件
	 * @param dir 将要删除的文件目录
	 * @return boolean Returns "true" if all deletions were successful.
	 *                 If a deletion fails, the method stops attempting to
	 *                 delete and returns "false".
	 */
	public static boolean deleteChildDir(File file, FilenameFilter filter) {
		if (!file.exists())
			return false;

		if (file.isDirectory()) {
			String[] children = file.list(filter);
			//递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(file, children[i]), filter);
				if (!success)
					return false;
			}
		}

		return false;
	}

	public static void copy(String from, String to) {
		File fromFile = new File(from);
		File toFile = new File(to);

		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(fromFile);
			output = new FileOutputStream(toFile);
			copyLarge(input, output);
		} catch (Throwable e) {
			LogUtils.error(e.getMessage(), e);
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					LogUtils.error(e.getMessage(), e);
				}
			if (output != null)
				try {
					output.close();
				} catch (IOException e) {
					LogUtils.error(e.getMessage(), e);
				}
		}
	}

	private static long copyLarge(InputStream input, OutputStream output) throws IOException {
		return copyLarge(input, output, new byte[4096]);
	}

	private static long copyLarge(InputStream input, OutputStream output, byte buffer[]) throws IOException {
		long count = 0L;
		for (int n = 0; -1 != (n = input.read(buffer));) {
			output.write(buffer, 0, n);
			count += n;
		}

		return count;
	}

	/**
	 * 读取文件内容
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static String readString(Reader input) throws IOException {
		BufferedReader reader = toBufferedReader(input);
		StringBuilder sb = new StringBuilder();
		for (String line = reader.readLine(); line != null; line = reader.readLine())
			sb.append(line + "\n");

		return sb.toString();
	}

	/**
	 * 读取文件内容
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static List<String> readLines(Reader input) throws IOException {
		BufferedReader reader = toBufferedReader(input);
		List<String> list = new ArrayList<String>();
		for (String line = reader.readLine(); line != null; line = reader.readLine())
			list.add(line);

		return list;
	}

	private static BufferedReader toBufferedReader(Reader reader) {
		return (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);
	}

	public static void write(String data, Writer output) throws IOException {
		if (data != null)
			output.write(data);
	}

	public static void write(String data, OutputStream output) throws IOException {
		if (data != null)
			output.write(data.getBytes());
	}

	//用户行为调用
	// 读取指定路径文本文件  
	public static String read(String filePath) {
		StringBuilder str = new StringBuilder();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(filePath));
			String s;
			try {
				while ((s = in.readLine()) != null)
					str.append(s);
			} finally {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str.toString();
	}

	// 写入指定的文本文件，append为true表示追加，false表示重头开始写，  
	//text是要写入的文本字符串，text为null时直接返回  
	public static void write(String filePath, boolean append, String text) {
		if (text == null)
			return;
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filePath, append));
			try {
				out.write(text);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Java文件操作 获取文件扩展名 
	 * @param filename
	 * @return
	 */
	public static String getExtName(File file, boolean isWithDot) {
		if (file == null || !file.exists()) {
			return null;
		}

		return getExtName(file.getName(), isWithDot);
	}

	/**
	 * Java文件操作 获取文件扩展名 
	 * @param filename
	 * @return
	 */
	public static String getExtName(String fileName, boolean isWithDot) {
		if (StringUtils.isBlank(fileName)) {
			return null;
		}

		if ((fileName != null) && (fileName.length() > 0)) {
			int dot = fileName.lastIndexOf('.');
			if ((dot > -1) && (dot < (fileName.length() - 1))) {
				return fileName.substring(dot + (isWithDot ? 0 : 1));
			}
		}

		return fileName;
	}

	/**
	 * 是否存在扩展名
	 * @param fileName
	 * @return
	 */
	public static boolean existsExtName(String fileName) {

		return StringUtils.isNotBlank(fileName) && (fileName.indexOf('.') != -1);
	}

	private static long size = 0;
	//统计目录大小的方法
	public static long getFileSize(File file){
		size=0;
		getDirSize(file);
		return size;
	}

	private static void getDirSize(File file) {
		if(file.isFile()) {
			//如果是文件，获取文件大小累加
			size += file.length();
		}else if(file.isDirectory()) {
			//获取目录中的文件及子目录信息
			File[] f1 = file.listFiles();
			for(int i = 0; i < f1.length; i++) {
				//调用递归遍历f1数组中的每一个对象
				getDirSize(f1[i]);
			}
		}
	}


}
