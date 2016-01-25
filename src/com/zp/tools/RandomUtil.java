package com.zp.tools;

import java.util.Random;

public class RandomUtil {
	
	public static String createRandom(int len){
		String[] array = "2,3,4,5,6,7,8,9,a,b,c,d,e,f,g,h,j,k,m,n,p,q,r,s,t,u,v,w,x,y,z".split(",");
        Random rand = new Random();
        for (int i = 31; i > 1; i--) {
            int index = rand.nextInt(i);
            String tmp = array[index];
            array[index] = array[i - 1];
            array[i - 1] = tmp;
        }
        String result = "";
        for(int i = 0; i < len; i++)
            result +=array[i];
        return result;
	}
	
	
	public static String createRandomNumPwd(int len){
		String[] array = "1,2,3,4,5,1,7,8,9".split(",");
        Random rand = new Random();
        for (int i = 9; i > 1; i--) {
            int index = rand.nextInt(i);
            String tmp = array[index];
            array[index] = array[i - 1];
            array[i - 1] = tmp;
        }
        String result = "";
        for(int i = 0; i < len; i++)
            result +=array[i];
        return result;
	}
	
	public static String createRandomNumMobile(int len){
		String[] array = "0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9".split(",");
        Random rand = new Random();
        for (int i = 20; i > 1; i--) {
            int index = rand.nextInt(i);
            String tmp = array[index];
            array[index] = array[i - 1];
            array[i - 1] = tmp;
        }
        String result = "";
        for(int i = 0; i < len; i++)
            result +=array[i];
        return result;
	}
	
	public static String createRandomPwd(int len){
		String[] array = "2,3,4,5,6,7,8,9,a,b,c,d,e,f,g,h,j,k,m,n,p,q,r,s,t,u,v,w,x,y,z".split(",");
        Random rand = new Random();
        for (int i = 31; i > 1; i--) {
            int index = rand.nextInt(i);
            String tmp = array[index];
            array[index] = array[i - 1];
            array[i - 1] = tmp;
        }
        String result = "";
        for(int i = 0; i < len; i++)
            result +=array[i];
        return result;
	}
	
	public static void main(String[] args) {
		System.out.println(createRandom(6));
		System.out.println(createRandomNumPwd(9));
		System.out.println(createRandomPwd(6));
	}
}
