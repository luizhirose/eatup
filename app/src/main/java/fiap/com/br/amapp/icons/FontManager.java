package fiap.com.br.amapp.icons;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Hashtable;

public class FontManager {
        private static Hashtable<String,Typeface> cachedIcons = new Hashtable<>();

        public static final String ROOT = "fonts/", FONTAWESOME = ROOT + "ionicons.ttf";

        public static Typeface getIcons(String path, Context context){
            Typeface icons = cachedIcons.get(path);

            if (icons == null){
                try {
                    icons = Typeface.createFromAsset(context.getAssets(), path);
                }catch (Exception e){
                    return null;
                }

                cachedIcons.put(path, icons);
            }

            return icons;
        }

        public static void markAsIconContainer(View v, Typeface typeface) {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    markAsIconContainer(child, typeface);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(typeface);
            }
        }

}
