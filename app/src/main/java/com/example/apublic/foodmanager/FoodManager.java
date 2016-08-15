package com.example.apublic.foodmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;

public class FoodManager extends AppCompatActivity {
    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

    FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_manager);

        addButton = (FloatingActionButton) findViewById( R.id.addButton );
        viewFood();

        addButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ){
                // インテントの生成
                Intent intent = new Intent(FoodManager.this, AddFood.class);
                startActivityForResult(intent, 0);
                // viewFood();
                // toAddView();
            }
        });
    }

    // トップ画面を開く際にDBからすべての食材のデータを表示する.
    private void viewFood(){
        LinearLayout onlyFoodListL  = new LinearLayout( this );     // 一つの食材をイメージを縦2行分にして右に賞味期限,名称を2行にして表示するためレイアウト
        ( (LinearLayout) findViewById( R.id.foodListL ) ).addView( onlyFoodListL, createParam(MP, MP) );
        ImageView foodImage = new ImageView( this );
        LinearLayout foodDataL  = new LinearLayout( this );         // 食材データ(賞味(消費)期限,名称)を縦に表示するためのレイアウト

        // 食品表示の全体のマージンを設定するための変数
        ViewGroup.LayoutParams allListLP = onlyFoodListL.getLayoutParams();
        ViewGroup.MarginLayoutParams allListMLP = (ViewGroup.MarginLayoutParams)allListLP;

        // 食品情報一つのマージン設定 上下にマージンを取る.
        allListMLP.setMargins(0, 10, 0, 30);
        onlyFoodListL.setLayoutParams( allListMLP );

        // 食品のイメージをサイズ指定して表示
        onlyFoodListL.addView( foodImage, createParam(192, 192) );
        // 食品の情報( 賞味期限, 食品名 )を表示
        onlyFoodListL.addView( foodDataL, createParam(MP, WC) );

        // 食品画像のマージンを設定するための変数
        ViewGroup.LayoutParams FoodImageLP = foodImage.getLayoutParams();
        ViewGroup.MarginLayoutParams FoodImageMLP = (ViewGroup.MarginLayoutParams)FoodImageLP;

        // 食品画像の右にマージンを取る
        FoodImageMLP.setMargins(0, 0, 20, 0);
        foodImage.setLayoutParams( FoodImageMLP );

        TextView foodName = new TextView( this );
        TextView foodExDate = new TextView( this );                 // 消費期限のテキスト
        foodDataL.addView( foodName, createParam(WC, 192/2 ) );
        foodDataL.addView( foodExDate, createParam(WC, 192/2 ) );

        onlyFoodListL.setOrientation( LinearLayout.HORIZONTAL );
        foodDataL.setOrientation( LinearLayout.VERTICAL );

        foodImage.setImageResource( R.drawable.meet );

        foodName.setText( "Name" );
        foodExDate.setText( "ExDate" );
    }

    private LinearLayout.LayoutParams createParam(int w, int h){
        return new LinearLayout.LayoutParams(w, h);
    }
}
