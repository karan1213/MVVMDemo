package com.mvvm.kipl.mvvmdemo.binding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mvvm.kipl.mvvmdemo.R;
import com.mvvm.kipl.mvvmdemo.base.itemdecorator.DividerItemDecoration;
import com.mvvm.kipl.mvvmdemo.util.Constant;
import com.mvvm.kipl.mvvmdemo.util.StringUtility;

import org.joda.time.format.DateTimeFormat;


/**
 * Created by Admin on 01-08-2016.
 */
public class DataBindingAdapter {

    @BindingAdapter({"bind:font"})
    public static void setFont(TextView textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }

    @BindingAdapter({"bind:font1"})
    public static void setFont1(TextInputLayout textView, String fontName) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/" + fontName));
    }


    @BindingAdapter({"bind:orientation", "bind:divider"})
    //set orientation Horizontal(0) or Vertical(1)
    public static void setDivider(RecyclerView recyclerView, int orientation, Drawable divider) {
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), orientation, divider));
    }

    @BindingAdapter({"bind:src_url", "bind:crop_circle"})
    public static void setSrcUrl(final ImageView view, final String sourceUrl, final boolean cropCircle) {
        if (StringUtility.validateString(sourceUrl)) {
            if (cropCircle) {
                Glide.with(view
                        .getContext())
                        .load(sourceUrl)
                        .transform(new CircleTransform(view.getContext()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .crossFade()
//                    .animate(android.R.anim.fade_in)
                        .into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                view.setImageDrawable(resource);
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                super.onLoadFailed(e, errorDrawable);
                            }
                        });
            } else {
                Glide.with(view.getContext())
                        .load(sourceUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.no_preview)
                        .error(R.drawable.no_preview)
//                    .crossFade()
//                    .animate(android.R.anim.fade_in)
                        .into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                view.setImageDrawable(resource);
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                super.onLoadFailed(e, errorDrawable);
                            }
                        });
            }
        }
    }

    @BindingAdapter({"bind:src_url"})
    public static void setSrcUrl(final ImageView view, String sourceUrl) {
        Log.d(DataBindingAdapter.class.getSimpleName(), "setSrcUrl: " + sourceUrl);
        Glide.with(view.getContext()).load(sourceUrl)
//                .placeholder(R.drawable.no_preview)
                .error(R.drawable.no_preview)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        view.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                    }
                });
    }

    @BindingAdapter({"bind:src_res", "bind:crop_circle"})
    public static void setCropCircle(final ImageView view, final int src, final boolean cropCircle) {
        Glide.with(view.getContext())
                .load(src).transform(cropCircle ? new CircleTransform(view.getContext()) : null)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.no_preview)
                .error(R.drawable.no_preview).crossFade()
                .animate(android.R.anim.fade_in)
                .into(view);

    }


    @BindingAdapter({"bind:src_res", "bind:crop_circle"})
    public static void setCropCircle(final ImageView view, final String src, final boolean cropCircle) {
        Glide.with(view.getContext())
                .load(src).transform(cropCircle ? new CircleTransform(view.getContext()) : null)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.no_preview)
                .error(R.drawable.no_preview).crossFade()
                .animate(android.R.anim.fade_in)
                .into(view);

    }

    @BindingAdapter({"bind:date"})
    public static void setDate(TextView view, long date) {
        if (date > 0) {
            setDate(view, date, Constant.DATE_FORMAT_MM_DD_YYYY);
        } else {
            view.setText("");
        }
    }

    @BindingAdapter({"bind:date", "bind:date_format"})
    public static void setDate(TextView view, long timeStamp, String dateFormat) {
        if (timeStamp > 0) {
            view.setText(DateTimeFormat.forPattern(dateFormat).print(timeStamp * 1000L));
        } else {
            view.setText("");
        }
    }

    @BindingAdapter({"bind:entries","bind:spinner_item_layout", "bind:spinner_drop_down_layout"})
    public static void setAdapter(Spinner spinner, String[] entries, @LayoutRes int spinner_item_layout, @LayoutRes int spinner_drop_down_layout) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(spinner.getContext(), spinner_item_layout, entries){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (view instanceof TextView) {
                    //setFont(((TextView) view), view.getContext().getString(R.string
                      //  .circular_std_bold));
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view =  super.getDropDownView(position, convertView, parent);
                if (view instanceof TextView) {
                    //setFont(((TextView) view), view.getContext().getString(R.string
                       // .circular_std_bold));
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(spinner_drop_down_layout);
        spinner.setAdapter(adapter);
    }

    @BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public static class CircleTransform extends BitmapTransformation {
        public CircleTransform(Context context) {
            super(context);
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return result;
        }

        @Override
        public String getId() {
            return getClass().getName();
        }
    }
}
