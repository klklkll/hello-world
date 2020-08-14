package hello;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import java.net.URLConnection;


public class DownUtil {
	private String path;//������Դ·��
	private String targetFile;//�����ļ�����λ��
	private int threadNum;//�߳�������Դ�ĸ���
	private DownThread[] threads;//�����̶߳���
	private int filesize;//�����ļ��ܴ�С
	
	public DownUtil(String path,String targetFile,int threadNum) {
		this.path=path;
		this.targetFile=targetFile;
		this.threadNum=threadNum;
		//��ʼ��threads�߳�
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
		private int startpos;//��ǰ����λ��
		private int currentPartSize;//	�����ļ�����
		private RandomAccessFile currentPart;//��Ҫ���ص��ļ���
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
				inStream = conn.getInputStream(); // �õ����緵�ص�������
				inStream.skip(this.startpos);//����startpos���ֽڣ�ֻ���������Լ��߳��ǲ����ļ�
				byte[] buffer=new byte[1024];
				int hasRead=0;
				while(length<currentPartSize&&(hasRead=inStream.read(buffer)
//read(byte[] b) ���������ж�ȡһ���������ֽڣ�������洢�ڻ��������� b �С���������ʽ����ʵ�ʶ�ȡ���ֽ��������������ݿ��á���⵽�ļ�ĩβ�����׳��쳣ǰ���˷���һֱ������
//��� b �ĳ���Ϊ 0���򲻶�ȡ�κ��ֽڲ����� 0�����򣬳��Զ�ȡ����һ���ֽڡ������Ϊ��λ���ļ�ĩβ��û�п��õ��ֽڣ��򷵻�ֵ -1���������ٶ�ȡһ���ֽڲ�����洢�� b �С�
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
//    private String path;//������Դ·��
//    private String targetFile;//�����ļ�����λ��
//    private int threadNum;//�߳�������Դ�ĸ���
//    private DownThread[] threads;//�����̶߳���
//    private int filesize;//�����ļ��ܴ�С
//
//    public DownUtil(String path, String targetFile, int threadNum) {
//        this.path = path;
//        this.targetFile = targetFile;
//        this.threadNum = threadNum;
//        //��ʼ��threads�߳�
//        threads = new DownThread[threadNum];
//    }
//
//    public void download() throws Exception {
//        URL url = new URL(null, path, new sun.net.www.protocol.https.Handler());
//        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//        //����д��
//        conn.setDoOutput(true);
//        //�������
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
//        private int startpos;//��ǰ����λ��
//        private int currentPartSize;//	�����ļ�����
//        private RandomAccessFile currentPart;//��Ҫ���ص��ļ���
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
//                //����д��
//                conn.setDoOutput(true);
//                //�������
//                conn.setDoInput(true);
//                conn.setConnectTimeout(5 * 1000);
//                conn.setRequestMethod("GET");
//                conn.connect();
////                conn.setRequestProperty("Accept", "*/*");
////                conn.setRequestProperty("Accept=Language", "zh_CN");
////                conn.setRequestProperty("Charset", "UTF-8");
//                InputStream inStream;
//                inStream = conn.getInputStream(); // �õ����緵�ص�������
//                inStream.skip(this.startpos);//����startpos���ֽڣ�ֻ���������Լ��߳��ǲ����ļ�
//                byte[] buffer = new byte[1024];
//                int hasRead = 0;
//                while (length < currentPartSize && (hasRead = inStream.read(buffer)
////read(byte[] b) ���������ж�ȡһ���������ֽڣ�������洢�ڻ��������� b �С���������ʽ����ʵ�ʶ�ȡ���ֽ��������������ݿ��á���⵽�ļ�ĩβ�����׳��쳣ǰ���˷���һֱ������
////��� b �ĳ���Ϊ 0���򲻶�ȡ�κ��ֽڲ����� 0�����򣬳��Զ�ȡ����һ���ֽڡ������Ϊ��λ���ļ�ĩβ��û�п��õ��ֽڣ��򷵻�ֵ -1���������ٶ�ȡһ���ֽڲ�����洢�� b �С�
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