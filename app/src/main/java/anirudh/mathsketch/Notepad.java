package anirudh.mathsketch;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
        import android.widget.ImageButton;
import android.widget.ImageView;

public class Notepad extends AppCompatActivity {
            private DrawingView drawView;
            private ImageButton currPaint;
            private int prev=1;
            int[] colors=new int[4];
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                this.requestWindowFeature(Window.FEATURE_NO_TITLE);
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_notepad);
                ImageView prevstroke=(ImageView)findViewById(R.id.prevStroke);
                //Initialize the DrawingView
                drawView=(DrawingView) findViewById(R.id.drawingView);
                drawView.setImageView(prevstroke);
                drawView.invalidate();
                //Initialize all the color buttons and delete button
                final Button button1=(Button) findViewById(R.id.color1);
                final Button button2=(Button) findViewById(R.id.color2);
                final Button button3=(Button) findViewById(R.id.color3);
                final Button button4=(Button) findViewById(R.id.color4);
                final Button deleteButton=(Button) findViewById(R.id.delete);
                final Button[] list={button1,button2,button3,button4};
        //Initialize colors for different strokes
        colors[0]=ContextCompat.getColor(getApplicationContext(), R.color.color1);
        colors[1]=ContextCompat.getColor(getApplicationContext(), R.color.color2);
        colors[2]=ContextCompat.getColor(getApplicationContext(), R.color.color3);
        colors[3]=ContextCompat.getColor(getApplicationContext(), R.color.color4);
        unpress(list,prev);
        button1.setBackgroundResource(R.drawable.paint1_pressed);
        button1.setTag("pressed");
        prev=1;
        drawView.setColor(colors[0], true);

        //Make all the different color stroke buttons pretty repetitive and could be improved(I hope)
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getTag()=="pressed")
                    return;
                unpress(list,prev);
                button1.setBackgroundResource(R.drawable.paint1_pressed);
                button1.setTag("pressed");
                prev=1;
                drawView.setColor(colors[0], true);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getTag()=="pressed")
                    return;
                unpress(list,prev);
                button2.setBackgroundResource(R.drawable.paint2_pressed);
                button2.setTag("pressed");
                prev=2;
                drawView.setColor(colors[1], true);

            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getTag()=="pressed")
                    return;
                unpress(list,prev);
                button3.setBackgroundResource(R.drawable.paint3_pressed);
                button3.setTag("pressed");
                prev=3;
                drawView.setColor(colors[2], true);

            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getTag()=="pressed")
                    return;
                unpress(list,prev);
                button4.setBackgroundResource(R.drawable.paint4_pressed);
                button4.setTag("pressed");
                prev=4;
                drawView.setColor(colors[3], true);
            }
        });

        //Button to delete to the entire screen
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.undo();
            }
        });


    }
    //"Unpresses" a button so they dont stay pressed
    public void unpress(Button[] list, int index){
            int i=index-1;
            list[i].setTag("notpressed");
            if(i==0)
                list[i].setBackgroundResource(R.drawable.paint1);
            if(i==1)
                list[i].setBackgroundResource(R.drawable.paint2);
            if(i==2)
                list[i].setBackgroundResource(R.drawable.paint3);
            if(i==3)
                list[i].setBackgroundResource(R.drawable.paint4);

    }


}
