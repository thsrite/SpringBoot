package com.erin.sun.qrcode;


public class QrCodeTest {

    public static void main(String[] args) throws Exception {
        // 存放在二维码中的内容
        String text = "我是小铭";
        // 嵌入二维码的图片路径
//        String imgPath = "E:/qrCode/dog.jpg";
        // 生成的二维码的路径及名称
        String destPath = "E:/qrCode/qrcode/jam.jpg";
        //生成二维码
        QRCodeUtil.encode(text, null, destPath, true);
        // 解析二维码
        String str = QRCodeUtil.decode(destPath);
        // 打印出解析出的内容
        System.out.println(str);

    }

}
