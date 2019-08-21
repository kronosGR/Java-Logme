package android.print;

import android.os.Build;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * creates a pdf file from the printDocumentAdapter
 * have to be in android.print package
 */
public class PdfPrint {
    private static final String TAG = PdfPrint.class.getSimpleName();
    private final PrintAttributes printAttributes;
    private ParcelFileDescriptor mParcelFileDescriptor;

    public PdfPrint(PrintAttributes printAttributes) {
        this.printAttributes = printAttributes;
    }

    public void print(final PrintDocumentAdapter printAdapter, final File path, final String fileName, final CallbackPrint callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            printAdapter.onLayout(null, printAttributes, null, new PrintDocumentAdapter.LayoutResultCallback() {

                @Override
                public void onLayoutFinished(PrintDocumentInfo info, boolean changed) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        printAdapter.onWrite(new PageRange[]{PageRange.ALL_PAGES},
                                getOutputFile(path, fileName),
                                new CancellationSignal(),
                                new PrintDocumentAdapter.WriteResultCallback() {
                            @Override
                            public void onWriteFinished(PageRange[] pages) {
                                super.onWriteFinished(pages);
                              if (pages.length >0){
                                  File file = new File(path, fileName);
                                  String path = file.getAbsolutePath();
                                  try {
                                      mParcelFileDescriptor.close();
                                  } catch (IOException e) {
                                      e.printStackTrace();
                                  }
                                  file = null;
                                  callback.success(path);
                              } else {
                                  callback.onFailure();
                              }
                            }


                        });
                    }
                }
            }, null);

            printAdapter.onFinish();
        }
    }


    private ParcelFileDescriptor getOutputFile(File path, String fileName) {
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path, fileName);
        try {
            file.createNewFile();
            mParcelFileDescriptor = ParcelFileDescriptor.open(file,
                    ParcelFileDescriptor.MODE_READ_WRITE);
            return mParcelFileDescriptor;
        } catch (Exception e) {
            Log.e(TAG, "Failed to open ParcelFileDescriptor", e);
        }
        return null;
    }




    public interface CallbackPrint{
        void success(String path);
        void onFailure();
    }
}