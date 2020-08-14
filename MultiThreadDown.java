package hello;

public class MultiThreadDown {
	public static void main(String []args) 
	throws Exception{
		final DownUtil downUtil=new DownUtil("https://fkjava.org/2020/04/01/ssm/ssm_cover.png","ios.png",4);
		downUtil.download();
		new Thread(()->{
			while(downUtil.getCompleteRate()<1) {
				System.out.println("ÒÑÍê³É£º"+downUtil.getCompleteRate());
			}
		}).start();
	}
}
