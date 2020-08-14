package hello;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import java.net.URLConnection;


public class DownUtil {
	private String path;//下载资源路径
	private String targetFile;//下载文件保存位置
	private int threadNum;//线程下载资源的个数
	private DownThread[] threads;//下载线程对象
	private int filesize;//下载文件总大小
	
	public DownUtil(String path,String targetFile,int threadNum) {
		this.path=path;
		this.targetFile=targetFile;
		this.threadNum=threadNum;
		//初始化threads线程
		threads=new DownThread[threadNum];
	}
	
	public void download() throws Exception {
		URL url=new URL(null,path,new sun.net.www.protocol.https.Handler());
		HttpsURLConnection conn =(HttpsURLConnection)url.openConnection();
		conn.setConnectTimeout(5*1000);
		conn.setRequestMethod("GET");
//		conn.setRequestProperty("Accept", "*/*");
//		conn.setRequestProperty("Accept=Language", "zh_CN");
//		conn.setRequestProperty("Charset", "UTF-8");
//		conn.setRequestProperty("Connection", "Keep-Alive");
		filesize=conn.getContentLength();
		conn.disconnect();
		int currentPartSize=filesize/threadNum+1;
		RandomAccessFile file=new RandomAccessFile(targetFile,"rw");
		file.setLength(filesize);
		file.close();
		for(int i=0;i<threadNum;i++) {
			int startpos=i*currentPartSize;
			RandomAccessFile currentpart=new RandomAccessFile(targetFile,"rw");
			currentpart.seek(startpos);
			threads[i]=new DownThread(startpos,currentPartSize,currentpart);
			threads[i].start();
		}
	}
	
	public double getCompleteRate() {
		int sumSize=0;
		for(int i=0;i<threadNum;i++) {
			sumSize+=threads[i].length;
		}
		return sumSize*1.0/filesize;
	}
	
	private class DownThread extends Thread{
		private int startpos;//当前下载位置
		private int currentPartSize;//	下载文件个数
		private RandomAccessFile currentPart;//需要下载的文件块
		public int length;
		
		public DownThread(int startpos,int currentPartSize,RandomAccessFile currentPart) {
			this.startpos=startpos;
			this.currentPartSize=currentPartSize;
			this.currentPart=currentPart;
		}
		
		public void run() {
			try {
				URL url=new URL(null,path,new sun.net.www.protocol.https.Handler());
				HttpsURLConnection conn=(HttpsURLConnection)url.openConnection();
				conn.setConnectTimeout(5*1000);
				conn.setRequestMethod("GET");
//				conn.setRequestProperty("Accept", "*/*");
//				conn.setRequestProperty("Accept=Language", "zh_CN");
//				conn.setRequestProperty("Charset", "UTF-8");
				InputStream inStream;
				inStream = conn.getInputStream(); // 得到网络返回的输入流
				inStream.skip(this.startpos);//跳过startpos个字节，只负责下载自己线程那部分文件
				byte[] buffer=new byte[1024];
				int hasRead=0;
				while(length<currentPartSize&&(hasRead=inStream.read(buffer)
//read(byte[] b) 从输入流中读取一定数量的字节，并将其存储在缓冲区数组 b 中。以整数形式返回实际读取的字节数。在输入数据可用、检测到文件末尾或者抛出异常前，此方法一直阻塞。
//如果 b 的长度为 0，则不读取任何字节并返回 0；否则，尝试读取至少一个字节。如果因为流位于文件末尾而没有可用的字节，则返回值 -1；否则，至少读取一个字节并将其存储在 b 中。
						)!=-1) {
					currentPart.write(buffer,0,hasRead);
					length+=hasRead;
				}
				currentPart.close();
				inStream.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
//package hello;
//
//import java.io.InputStream;
//import java.io.RandomAccessFile;
//import java.net.URL;
//
//import javax.net.ssl.HttpsURLConnection;
//
//import java.net.URLConnection;
//
//
//public class DownUtil {
//    private String path;//下载资源路径
//    private String targetFile;//下载文件保存位置
//    private int threadNum;//线程下载资源的个数
//    private DownThread[] threads;//下载线程对象
//    private int filesize;//下载文件总大小
//
//    public DownUtil(String path, String targetFile, int threadNum) {
//        this.path = path;
//        this.targetFile = targetFile;
//        this.threadNum = threadNum;
//        //初始化threads线程
//        threads = new DownThread[threadNum];
//    }
//
//    public void download() throws Exception {
//        URL url = new URL(null, path, new sun.net.www.protocol.https.Handler());
//        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//        //允许写出
//        conn.setDoOutput(true);
//        //允许读入
//        conn.setDoInput(true);
//
//        conn.setConnectTimeout(5 * 1000);
//        conn.setRequestMethod("GET");
//        conn.connect();
////        conn.setRequestProperty("Accept", "*/*");
////        conn.setRequestProperty("Accept=Language", "zh_CN");
////        conn.setRequestProperty("Charset", "UTF-8");
////        conn.setRequestProperty("Connection", "Keep-Alive");
//        filesize = conn.getContentLength();
//        conn.disconnect();
//        int currentPartSize = filesize / threadNum + 1;
//        RandomAccessFile file = new RandomAccessFile(targetFile, "rw");
//        file.setLength(filesize);
//        file.close();
//        for (int i = 0; i < threadNum; i++) {
//            int startpos = i * currentPartSize;
//            RandomAccessFile currentpart = new RandomAccessFile(targetFile, "rw");
//            currentpart.seek(startpos);
//            threads[i] = new DownThread(startpos, currentPartSize, currentpart);
//            threads[i].start();
//        }
//    }
//
//    public double getCompleteRate() {
//        int sumSize = 0;
//        for (int i = 0; i < threadNum; i++) {
//            sumSize += threads[i].length;
//        }
//        return sumSize * 1.0 / filesize;
//    }
//
//    private class DownThread extends Thread {
//        private int startpos;//当前下载位置
//        private int currentPartSize;//	下载文件个数
//        private RandomAccessFile currentPart;//需要下载的文件块
//        public int length;
//
//        public DownThread(int startpos, int currentPartSize, RandomAccessFile currentPart) {
//            this.startpos = startpos;
//            this.currentPartSize = currentPartSize;
//            this.currentPart = currentPart;
//        }
//
//        public void run() {
//            try {
//                URL url = new URL(null, path, new sun.net.www.protocol.https.Handler());
//                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//                //允许写出
//                conn.setDoOutput(true);
//                //允许读入
//                conn.setDoInput(true);
//                conn.setConnectTimeout(5 * 1000);
//                conn.setRequestMethod("GET");
//                conn.connect();
////                conn.setRequestProperty("Accept", "*/*");
////                conn.setRequestProperty("Accept=Language", "zh_CN");
////                conn.setRequestProperty("Charset", "UTF-8");
//                InputStream inStream;
//                inStream = conn.getInputStream(); // 得到网络返回的输入流
//                inStream.skip(this.startpos);//跳过startpos个字节，只负责下载自己线程那部分文件
//                byte[] buffer = new byte[1024];
//                int hasRead = 0;
//                while (length < currentPartSize && (hasRead = inStream.read(buffer)
////read(byte[] b) 从输入流中读取一定数量的字节，并将其存储在缓冲区数组 b 中。以整数形式返回实际读取的字节数。在输入数据可用、检测到文件末尾或者抛出异常前，此方法一直阻塞。
////如果 b 的长度为 0，则不读取任何字节并返回 0；否则，尝试读取至少一个字节。如果因为流位于文件末尾而没有可用的字节，则返回值 -1；否则，至少读取一个字节并将其存储在 b 中。
//                ) != -1) {
//                    currentPart.write(buffer, 0, hasRead);
//                    length += hasRead;
//                }
//                currentPart.close();
//                inStream.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//}