package com.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;


public abstract class Utility
{
  public static DataElement retrieveReqDom(InputStream is)
    throws IOException, XmlException
  {
    byte[] bitLength = new byte[8];
    for (int i = 0; i < bitLength.length; i++) {
      bitLength[i] = ((byte)is.read());
    }
    int metaLength = Integer.parseInt(new String(bitLength));
    byte[] metaBytes = new byte[metaLength];
    int off = 0; int len = 0;
    while ((len = is.read(metaBytes, off, metaLength - off)) > 0) {
      off += len;
    }
    String meta = new String(metaBytes, "GBK");
    DataElement req = XmlManager.parse(meta);
    return req;
  }

  public static void writeRspDom(OutputStream os, DataElement rspDom)
    throws IOException
  {
    String rspXml = rspDom.toString();
   // byte[] rspBytes = rspXml.getBytes("GBK");
   // byte[] headBytes = encodeLength(rspBytes.length, 8);
    ObjectOutputStream oos = new ObjectOutputStream(os);
    oos.writeObject(rspXml);
    
    oos.close();
   // os.write(headBytes);
    //os.write(rspBytes);
  }

  public static byte[] encodeLength(int length, int bitLength)
  {
    byte[] consField = new byte[bitLength];
    for (int i = 0; i < consField.length; i++) {
      consField[i] = 48;
    }
    byte[] headerLengthBytes = String.valueOf(length).getBytes();
    System.arraycopy(headerLengthBytes, 0, consField, bitLength - headerLengthBytes.length, headerLengthBytes.length);
    return consField;
  }

}