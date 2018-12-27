package com.example.a21091.fourthproject;

import android.annotation.SuppressLint;
import android.app.FragmentBreadCrumbs;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.TextureView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends ListActivity {

    private static final int SELECT_PICTURE = 1;

    private ImageView img;

    private FileScanner fileScanner = new FileScanner();
    private String currentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private View selectedItem;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        this.getListView().setSelector(R.color.select_color);
        fillListView(currentPath);

        img = (ImageView)findViewById(R.id.img);

        this.getListView().setOnTouchListener(new OnSwipeTouchListener(this) {

            @Override
            protected void onLongPress() {

                if (selectedItem != null) {
                    TextView viewById = (TextView) selectedItem.findViewById(R.id.text_view);
                    String file = currentPath.concat("/" + viewById.getText().toString());
                    String newPathForCopy = fileScanner.getNewPathForCopy(file);
                    if (fileScanner.copyFileOrDirectory(file, newPathForCopy)) {
                        Toast.makeText(MainActivity.this, "Скопировано", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(MainActivity.this, "Ошибка копирования", Toast.LENGTH_SHORT).show();
                    }

                    fillListView(currentPath);
                }
            }

            @Override
            public void onSwipeRight() {
                if (selectedItem != null) {
                    onNextFile(selectedItem);
                }
            }

            @Override
            public void onSwipeLeft() {
                onPrevFile();
            }

            @Override
            public void onSwipeTop() {

            }

            @Override
            public void onSwipeBottom() {

            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        if (selectedItem != null) {
            selectedItem.setSelected(false);
        }
        selectedItem = v;
        selectedItem.setSelected(true);
        super.onListItemClick(l, v, position, id);
    }

    private void onNextFile(View v) {

        final TextView viewById = (TextView) v.findViewById(R.id.text_view);
        final String path = currentPath.concat("/" + viewById.getText());

        if (fileScanner.isDirectory(path)) {
            if (fileScanner.isAvailable(path)) {
                if (fillListView(path)) {
                    currentPath = path;
                    selectedItem = null;
                    Log.d("TAG", "Идём дальше!");
                }

            } else {
                Toast.makeText(this, "Папка защищена правами доступа", Toast.LENGTH_SHORT).show();
            }

        } else {

            DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface arg0, int arg1) {

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, Uri.parse("file://" + path));
                    intent.setType("image/*");
                    startActivity(intent);
                }
            };

            DialogInterface.OnClickListener cancelButtonListener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {

                }
            };

            new AlertDialog.Builder(this)
                    .setTitle("Подтверждение")
                    .setMessage("Хотите открыть файл " + path + "?")
                    .setPositiveButton("Да", okButtonListener)
                    .setNegativeButton("Нет", cancelButtonListener)
                    .show();
        }
            Log.d("TAG", "Файл " + viewById.getText());
        }

    private void onPrevFile() {

        String path = fileScanner.getParentPath(currentPath);

        if (path == null) {

            Toast.makeText(this, "Дальше ничего нет!", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Дальше ничего нет!!");

        } else {
            currentPath = path;
            fillListView(currentPath);
            selectedItem = null;
            Log.d("TAG", "Возвращаемся!");
        }
    }

    private boolean fillListView(String path) {

        List<File> files = fileScanner.get(path);
        Collections.sort(files, new Comparator<File>() {

            @Override
            public int compare(File file1, File file2) {

                if (file1.isDirectory() && file2.isDirectory()) {
                    if (file1.getName().trim().startsWith(".")) {
                        return 1;
                    } else if (file2.getName().trim().startsWith(".")) {
                        return -1;
                    }
                    return file1.getName().toLowerCase().compareTo(file2.getName().toLowerCase());

                } else if (file1.isDirectory()) {
                    return -1;
                } else if (file2.isDirectory()) {
                    return 1;
                }

                if (file1.getName().trim().startsWith(".")) {
                    return 1;
                } else if (file2.getName().trim().startsWith(".")) {
                    return -1;
                }
                return file1.getName().toLowerCase().compareTo(file2.getName().toLowerCase());
            }
        });

        if (files.isEmpty()) {
            Toast.makeText(this, "Папка пуста", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            setListAdapter(new CustomAdapter(this, files));
            return true;
        }
    }
}
