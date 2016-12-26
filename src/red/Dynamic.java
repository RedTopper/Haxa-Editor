package red;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class Dynamic {
	ArrayList<ByteBuffer> buffers = new ArrayList<>();
	
	public void putInt(int i) {
		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(i);
		buffers.add(buf);
	}
	
	public void putFloat(float f) {
		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putFloat(f);
		buffers.add(buf);
	}
	
	public void putChar(char c) {
		ByteBuffer buf = ByteBuffer.allocate(2);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putChar(c);
		buffers.add(buf);
	}
	
	public void putString(String string) {
		ByteBuffer buf = ByteBuffer.allocate(string.length() + 4);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(string.length()); 		
		buf.put(string.getBytes());
		buffers.add(buf);
	}

	public void putRawString(String string) {
		ByteBuffer buf = ByteBuffer.allocate(string.length());
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.put(string.getBytes());
		buffers.add(buf);
	}
	
	public void putColors(List<Color> bg1) {
		ByteBuffer buf = ByteBuffer.allocate(bg1.size() * 3 + 4);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(bg1.size());
		for(int i = 0; i < bg1.size(); i++) {
			buf.put((byte) bg1.get(i).getRed());
			buf.put((byte) bg1.get(i).getGreen());
			buf.put((byte) bg1.get(i).getBlue());
		}
		buffers.add(buf);
	}
	
	public void write(File file) throws IOException {
		
		//Delete really old file and move old file into it's place
		File oldDir = Util.getDir(new File(file.getParent() + Util.OLD));
		File backupFile = new File(oldDir, file.getName());
		backupFile.delete();
		file.renameTo(backupFile);
		
		//write new file
		FileOutputStream outputStream = new FileOutputStream(file, false);
		FileChannel channel = outputStream.getChannel();
		for(ByteBuffer buf : buffers) {
			buf.rewind();
			channel.write(buf);
		}
		channel.close();
		outputStream.close();
		System.out.println("Backed up and wrote binary file: '" + file.getAbsolutePath() + "'");
	}
}
