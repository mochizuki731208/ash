package com.web.processor;

import java.io.InputStream;
import java.io.OutputStream;

public abstract interface AbOrderProcessor
{
  public abstract void process(String paramString, InputStream paramInputStream, OutputStream paramOutputStream);
}