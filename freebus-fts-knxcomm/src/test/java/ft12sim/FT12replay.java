package ft12sim;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FT12replay {

	InputStream in;
	OutputStream out;
	ConvertTools convertTools;
	ReplayFrames replayFrames;

	public FT12replay(InputStream in, OutputStream out) throws Exception {
		this.in = in;
		this.out = out;
		convertTools = new ConvertTools();
		replayFrames = new ReplayFrames();
		listen();
	}

	public void listen() throws Exception {

		int len = 0;
		byte[] buffer = new byte[1024];
		int[] rxarr;
		int[][] txbuffer;
		ArrayList<Integer> rxfifo = new ArrayList<Integer>();
		int[][] replaybuffer = null;
		int replaycounter = 0;
		while ((len = in.read(buffer)) > -1) {

			for (int i = 0; i < len; i++)
				rxfifo.add(convertTools.unsignedByteToInt(buffer[i]));
			if (rxfifo.size() > 0) {
				boolean uncompliteTelegram = false;
				rxarr = new int[rxfifo.size()];

				for (int i = 0; i < rxfifo.size(); i++)
					rxarr[i] = rxfifo.get(i);
				if (rxarr.length >= 1) {
					if (rxarr[0] == 0xE5) {
						System.out.println("E5 ACK recive");
						rxfifo.remove(0);
						if (null != replaybuffer) {
							if (replaybuffer.length > (replaycounter + 1)) {
								replaycounter++;
								System.out
										.println("long Frame reply "
												+ convertTools
														.getHexString(replaybuffer[replaycounter]));
								for (int b : replaybuffer[replaycounter])
									out.write(b);
							}
						}
					}
				}

				if (rxarr.length >= 4) {
					if (rxarr[0] == 0x10 && rxarr[3] == 0x16) {
						System.out.println("Short Frame"
								+ convertTools.getHexString(rxarr));
						out.write(0xe5);
						if (rxarr[1] == 0x49) {
							out.write(0x10);
							out.write(0x0B);
							out.write(0x0B);
							out.write(0x16);
						}
						rxfifo.remove(0);
						rxfifo.remove(0);
						rxfifo.remove(0);
						rxfifo.remove(0);
					}
				}
				if (rxarr.length >= 7) {
					if (rxarr[0] == 0x68 && rxarr[3] == 0x68
							&& rxarr[1] == rxarr[2]) {

						if (rxarr.length > (rxarr[1] + 5)) {
							int a = rxarr[rxarr[1] + 5];
							int[] knxtelegramm = new int[rxarr[1] + 6];
							Thread.sleep(0);

							out.write(0xe5);
						System.arraycopy(rxarr, 0, knxtelegramm, 0,
									rxarr[1] + 6);
							System.out.println("long Frame "
									+ convertTools.getHexString(rxarr));

							Thread.sleep(0);
							int[][] ft12anser = replayFrames
									.getanswer(knxtelegramm);
							if (ft12anser != null) {
								if (ft12anser.length > 1) {
									replaybuffer = ft12anser;
									replaycounter = 0;
								}
								System.out.println("long Frame reply "
										+ convertTools
												.getHexString(ft12anser[0]));
								for (int b : ft12anser[0]) {
									Thread.sleep(0);
									out.write(b);
								}
							}
							for (int i = 0; i < (rxarr[1] + 6); i++)
								rxfifo.remove(0);
						} else {
							uncompliteTelegram = true;
						}
					}
				}
				if (rxarr[0] == 0x10)
					uncompliteTelegram = true;
				if (uncompliteTelegram == false && rxfifo.size() > 0) {
					if (rxfifo.get(0) != 0x10 && rxfifo.get(0) != 0x68
							&& rxfifo.get(0) != 0xe5)
						rxfifo.remove(0);
				}

			}

		}

	}
}
