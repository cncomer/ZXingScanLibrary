package com.google.zxing;
/**
 * Quoted-Printable也是MIME邮件中常用的编码方式之一。同Base64一样，它也将输入的字符串或数据编码成全是ASCII码的可打印字符串。
 * Quoted-Printable编码的基本方法是：输入数据在33-60、62-126范围内的，直接输出；其它的需编码为“=”加两个字节的HEX码(大写)。
 * 为保证输出行不超过规定长度(76个字符)，在行尾加“=\r\n”序列作为软回车。 
 */


import java.io.UnsupportedEncodingException;

/**
 * 
 * @author yeluosuifeng2005@gmail.com (陈凯)
 *
 */
public class QuotedPrintableDecoder {

	public QuotedPrintableDecoder() {

	}

	static public String EncodeQuoted(String pSrc) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < pSrc.length(); i++) {
			if(i%76==0) {//为保证输出行不超过规定长度(76个字符)，在行尾加“=\r\n”序列作为软回车
				result.append("=\r\n");
			}
			char ch = pSrc.charAt(i);
			if ((ch >= '!') && (ch <= '~') && (ch != '=')) {
				// ASCII 33-60, 62-126原样输出，其余的需编码
				result.append(ch);
			} else {
				String string =Integer.toHexString(ch).toUpperCase();
				if(string.length()==1)result.append("=0" + Integer.toHexString(ch).toUpperCase());
				else result.append('=' + Integer.toHexString(ch).toUpperCase());
			}
		}
		result.append("\r\n");
		return result.toString();
	}

	/**
	 * Quoted-Printable解码很简单，将编码过程反过来就行了。
	 * 
	 * @param pSrc
	 */
	static public String DecodeQuoted(String pSrc) {
		pSrc = pSrc.replace("=\n", "");
		int i = 0;
		StringBuffer sb = new StringBuffer();
		while (i < pSrc.length()) {
			char ch = pSrc.charAt(i);
			if (ch == '=') {// 是编码字节
//				if (i+1 < pSrc.length()) {
//					char lfcr = pSrc.charAt(i+1);
//					if (lfcr == '\n') {
//						i++;
//						continue;
//					}
//				}

				int toChar = Integer.parseInt(pSrc.substring(i+1 , i + 3), 16);
				sb.append((char) toChar);
				i += 3;
			} else { // 非编码字节
				sb.append(ch);
				i++;
			}
		}
		try {
			return new String(sb.toString().replace("\r", "").getBytes("ISO8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return null;// 解析失败返回null
		}
	}

//	public static void main(String[] args) {
//		String encoded = "=\r\n"
//				+ "200072=20=E4=B8=8A=E6=B5=B7=E5=B8=82=E9=97=B8=E5=8C=97=E5=8C=BA=E5=B9=BF=E=\r\n"+
//				"4=B8=AD=E8=A5=BF=E8=B7=AF757=E5=8F=B7=E5=A4=9A=E5=AA=92=E4=BD=93=E5=A4=A7=E5=8E" +
//				"=A69=E6=A5=BC@31=C2=B016'45.76\",121=C2=B026'07.65\"";
//		String decoded="0〜31及127(共33个)是控制字符或通信专用字符（其余为可显示字符），如控制符：LF（换行）、CR（回车）、FF（换页）、DEL（删除）、BS（退格)、BEL（振铃）等；通信专用字符：SOH（文头）、EOT（文尾）、ACK（确认）等；ASCII值为 8、9、10 和 13 分别转换为退格、制表、换行和回车字符。它们并没有特定的图形显示，但会依不同的应用程序，而对文本显示有不同的影响。32〜126(共95个)是字符(32sp是空格），其中48〜57为0到9十个阿拉伯数字； 　　65〜90为26个大写英文字母，97〜122号为26个小写英文字母，其余为一些标点符号、运算符号等。 　　同时还要注意，在标准ASCII中，其最高位(b7)用作奇偶校验位。所谓奇偶校验，是指在代码传送过程中用来检验是否出现错误的一种方法，一般分奇校验和偶校验两种。奇校验规定：正确的代码一个字节中1的个数必须是奇数，若非奇数，则在最高位b7添1；偶校验规定：正确的代码一个字节中1的个数必须是偶数，若非偶数，则在最高位b7添1。 　　后128个称为扩展ASCII码，目前许多基于x86的系统都支持使用扩展（或“高”）ASCII。扩展 ASCII 码允许将每个字符的第 8 位用于确定附加的 128 个特殊符号字符、外来语字母和图形符号。";
//
//		String test;
//		try {
//			test = QuotedPrintableDecoder.EncodeQuoted(new String(decoded.getBytes("UTF-8"),"ISO8859-1"));
//			System.out.println(test);
//			String sb = QuotedPrintableDecoder.DecodeQuoted(test);
//			System.out.println(sb);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//
//	}

}
