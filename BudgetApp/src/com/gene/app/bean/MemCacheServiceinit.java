package com.gene.app.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.google.appengine.api.memcache.AsyncMemcacheService;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class MemCacheServiceinit {

	public byte[] getAsyncCacheData(String key) throws IllegalArgumentException, InterruptedException, ExecutionException {
		 byte[] syncValue;
		 AsyncMemcacheService asyncCache = MemcacheServiceFactory.getAsyncMemcacheService();
		 Future<Object> futureValue = asyncCache.get(key);
		 syncValue = (byte[]) futureValue.get();
		 return syncValue;
	}
	
	public byte[] getsyncCacheData(String key) throws IllegalArgumentException, IOException {
		 byte[] ayncValue;
		 MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		 ayncValue = (byte[]) syncCache.get(key);
		 return ayncValue;
	}
	
	public boolean putAsyncCacheData(String key,byte[] asyncValue) throws IllegalArgumentException, IOException, InterruptedException, ExecutionException {
		 AsyncMemcacheService asyncCache = MemcacheServiceFactory.getAsyncMemcacheService();
		 asyncCache.put(key, asyncValue);
		 return true;
	}
	
	public boolean putsyncCacheData(String key,byte[] syncValue) throws IllegalArgumentException, IOException {
		 MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		 syncCache.put(key, syncValue);
		 return true;
	}
	
	
	public byte[] convertListToByte(List listOfObjects) throws IllegalArgumentException, IOException {
		ByteArrayOutputStream bytesArrayOutput = new ByteArrayOutputStream();
		ObjectOutputStream bytesObjectOutput = new ObjectOutputStream(bytesArrayOutput);
		bytesObjectOutput.writeObject(listOfObjects);
		byte[] returnBytes = bytesArrayOutput.toByteArray();
		return returnBytes;
	}
	public byte[] convertMapToByte(Map mapOfObjects) throws IllegalArgumentException, IOException {
		ByteArrayOutputStream bytesArrayOutput = new ByteArrayOutputStream();
		ObjectOutputStream bytesObjectOutput = new ObjectOutputStream(bytesArrayOutput);
		bytesObjectOutput.writeObject(mapOfObjects);
		byte[] returnBytes = bytesArrayOutput.toByteArray();
		return returnBytes;
	}
	public List convertByteToList(byte[] byteArray) throws IllegalArgumentException, IOException, ClassNotFoundException {
		ByteArrayInputStream inputByteArray = new ByteArrayInputStream(byteArray);
	    ObjectInputStream returnList  = new ObjectInputStream(inputByteArray);
	    return (List) returnList.readObject();
	}
	
	
	
	public Map convertByteToMap(byte[] byteArray) throws IllegalArgumentException, IOException, ClassNotFoundException {
		ByteArrayInputStream inputByteArray = new ByteArrayInputStream(byteArray);
	    ObjectInputStream returnList  = new ObjectInputStream(inputByteArray);
	    return (Map) returnList.readObject();
	}
	public boolean deleteCacheData(String key) throws IllegalArgumentException, IOException, InterruptedException, ExecutionException {
		 MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		 boolean tr =syncCache.delete((Object)key);
		 System.out.println("Deleted"+tr);
		 return tr;
	}
	public boolean clearCacheData() throws IllegalArgumentException, IOException, InterruptedException, ExecutionException {
		 MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		 syncCache.clearAll();
		 return true;
	}
	
	public byte[] convertObjectToByte(Object obj) throws IllegalArgumentException, IOException {
		ByteArrayOutputStream bytesArrayOutput = new ByteArrayOutputStream();
		ObjectOutputStream bytesObjectOutput = new ObjectOutputStream(bytesArrayOutput);
		bytesObjectOutput.writeObject(obj);
		byte[] returnBytes = bytesArrayOutput.toByteArray();
		return returnBytes;
	}
	
	public Object convertByteToObject(byte[] byteArray) throws IllegalArgumentException, IOException {
		ByteArrayInputStream inputByteArray = new ByteArrayInputStream(byteArray);
	    ObjectInputStream returnList  = new ObjectInputStream(inputByteArray);
	    try {
			return (Object) returnList.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}

