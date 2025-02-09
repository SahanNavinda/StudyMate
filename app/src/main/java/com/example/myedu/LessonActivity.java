package com.example.myedu;



import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LessonActivity extends AppCompatActivity {

    private ListView lessonList;
    private ProgressBar progressBar;
    private TextView progressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        lessonList = findViewById(R.id.lessonList);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        // Load lessons (dummy data for now)
        String[] lessons = {"Lesson 1: Introduction", "Lesson 2: Basics", "Lesson 3: Advanced Topics"};
        String[] lessonUrls = {"https://example.com/lesson1", "https://example.com/lesson2", "https://example.com/lesson3"};
        LessonAdapter adapter = new LessonAdapter(this, lessons, lessonUrls);
        lessonList.setAdapter(adapter);

        lessonList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(lessonUrls[position]));
            startActivity(intent);
        });

        updateProgress();
    }

    private void updateProgress() {
        int completedLessons = 1;
        int totalLessons = 3;
        int progress = (completedLessons * 100) / totalLessons;
        progressBar.setProgress(progress);
        progressText.setText("Progress: " + progress + "%");
    }
}

class LessonAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] lessons;
    private final String[] lessonUrls;

    public LessonAdapter(Context context, String[] lessons, String[] lessonUrls) {
        super(context, android.R.layout.simple_list_item_1, lessons);
        this.context = context;
        this.lessons = lessons;
        this.lessonUrls = lessonUrls;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(lessons[position]);
        return view;
    }
}
