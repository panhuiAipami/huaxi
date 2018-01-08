package com.spriteapp.booklibrary.ui.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.custom.SpaceGridDecoration;
import com.spriteapp.booklibrary.ui.adapter.PhotoSelectedListAdapter;
import com.spriteapp.booklibrary.util.Util;

import java.util.ArrayList;
import java.util.List;


/**
 * 发动态
 */
public class CreateDynamicActivity extends TitleActivity {
    private final int MAX_COUNTS = 5;
    TextView title;
    ImageView iv_submit;
    TextView text_length;

    EditText input_text;
    RecyclerView recycler_view_photo;
    PhotoSelectedListAdapter adapter;
    List<LocalMedia> selectList = new ArrayList<>();
    private ImageView iv_back,add_photo;


    public void initViewFromXML() throws Exception {
        setContentView(R.layout.activity_create_dynamic);
        title = (TextView) findViewById(R.id.title);
        text_length = (TextView) findViewById(R.id.text_length);
        iv_submit = (ImageView) findViewById(R.id.iv_submit);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        add_photo = (ImageView) findViewById(R.id.add_photo);
        input_text = (EditText) findViewById(R.id.input_text);
        recycler_view_photo = (RecyclerView) findViewById(R.id.recycler_view_photo);
        recycler_view_photo.addItemDecoration(new SpaceGridDecoration(Util.dp2px(this, 10)));

    }


    public void initData(){
        adapter = new PhotoSelectedListAdapter(this);
        recycler_view_photo.setAdapter(adapter);
        recycler_view_photo.setLayoutManager(new GridLayoutManager(this, 3));
    }



    public void initListener() throws Exception {
        iv_back.setOnClickListener(this);
        add_photo.setOnClickListener(this);
        iv_submit.setOnClickListener(this);
        input_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    text_length.setText((start + count) + "/140");
                } else {
                    text_length.setText("0/140");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void addContentView() {
        try {
            initViewFromXML();
            initData();
            initListener();



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v==iv_back){
            finish();
        }else if(v==add_photo){//添加照片
            PictureSelector.create(CreateDynamicActivity.this)
                    .openGallery(PictureMimeType.ofImage())
                    .selectionMedia(selectList)
                    .compress(true)
                    .maxSelectNum(MAX_COUNTS)
                    .forResult(PictureConfig.CHOOSE_REQUEST);
        }else if(v==iv_submit){//发布

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    selectList = PictureSelector.obtainMultipleResult(data);
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
//    // 进入相册 以下是例子：用不到的api可以不写
// PictureSelector.create(MainActivity.this)
//            .openGallery()//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
// 	.theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
// 	.maxSelectNum()// 最大图片选择数量 int
// 	.minSelectNum()// 最小选择数量 int
//	.imageSpanCount(4)// 每行显示个数 int
// 	.selectionMode()// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
// 	.previewImage()// 是否可预览图片 true or false
// 	.previewVideo()// 是否可预览视频 true or false
//	.enablePreviewAudio() // 是否可播放音频 true or false
// 	.isCamera()// 是否显示拍照按钮 true or false
//	.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
//	.isZoomAnim(true)// 图片列表点击 缩放效果 默认true
//	.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
//	.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
// 	.enableCrop()// 是否裁剪 true or false
// 	.compress()// 是否压缩 true or false
// 	.glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
// 	.withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
// 	.hideBottomControls()// 是否显示uCrop工具栏，默认不显示 true or false
// 	.isGif()// 是否显示gif图片 true or false
//	.compressSavePath(getPath())//压缩图片保存地址
//            .freeStyleCropEnabled()// 裁剪框是否可拖拽 true or false
// 	.circleDimmedLayer()// 是否圆形裁剪 true or false
// 	.showCropFrame()// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
// 	.showCropGrid()// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
// 	.openClickSound()// 是否开启点击声音 true or false
// 	.selectionMedia()// 是否传入已选图片 List<LocalMedia> list
// 	.previewEggs()// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
// 	.cropCompressQuality()// 裁剪压缩质量 默认90 int
// 	.minimumCompressSize(100)// 小于100kb的图片不压缩
// 	.synOrAsy(true)//同步true或异步false 压缩 默认同步
// 	.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
// 	.rotateEnabled() // 裁剪是否可旋转图片 true or false
// 	.scaleEnabled()// 裁剪是否可放大缩小图片 true or false
// 	.videoQuality()// 视频录制质量 0 or 1 int
//	.videoMaxSecond(15)// 显示多少秒以内的视频or音频也可适用 int
//        .videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int
//	.recordVideoSecond()//视频秒数录制 默认60s int
// 	.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
}
