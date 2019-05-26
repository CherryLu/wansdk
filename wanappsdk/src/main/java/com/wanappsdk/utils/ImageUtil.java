package com.wanappsdk.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.HashMap;
import java.util.Map;

public class ImageUtil {
    /**
     * 识别二维码
     * @param imageView
     */
    public static void distinguishQRcode(final ImageView imageView, final QcCallBack qcCallBack){
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Bitmap obmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                int width = obmp.getWidth();
                int height = obmp.getHeight();
                int[] data = new int[width * height];
                obmp.getPixels(data, 0, width, 0, 0, width, height);
                RGBLuminanceSource source = new RGBLuminanceSource(width, height, data);
                BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
                QRCodeReader reader = new QRCodeReader();

                Map<DecodeHintType, Object> qrParams = new HashMap<>();

                qrParams.put(DecodeHintType.CHARACTER_SET, "UTF-8");

                qrParams.put(DecodeHintType.TRY_HARDER, true);

                qrParams.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
                qrParams.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);

                Result re = null;
                try {
                    re = reader.decode(bitmap1,qrParams);
                } catch (NotFoundException e) {
                    e.printStackTrace();
                } catch (ChecksumException e) {
                    e.printStackTrace();
                } catch (FormatException e) {
                    e.printStackTrace();
                }
                if (re == null) {
                    //二维码是空
                    //showAlert(obmp);
                    qcCallBack.success("");
                } else {
                    qcCallBack.success(re.getText());
                    //识别出来了
                    //showSelectAlert(obmp, re.getText());
                }
                return false;
            }
        });
    }
}
