package org.freebus.knxcomm.application.devicedescriptor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.freebus.knxcomm.application.DeviceDescriptorResponse;

/**
 * A device descriptor. Used in {@link DeviceDescriptorResponse}.
 * 
 * @see DeviceDescriptor0
 * @see DeviceDescriptor2
 */
public interface DeviceDescriptor
{
   /**
    * @return the numerical descriptor type.
    */
   public int getTypeCode();

   /**
    * Initialize the device descriptor from a {@link DataInput data input}
    * stream.
    * 
    * @param in - the input stream to read.
    * @param length - the number of bytes that the application shall read
    * 
    * @throws IOException
    */
   void readData(DataInput in, int length) throws IOException;

   /**
    * Write the device descriptor to a {@link DataOutput data output} stream.
    * 
    * @param out - the output stream to write to.
    *
    * @throws IOException
    */
   void writeData(DataOutput out) throws IOException;
}
