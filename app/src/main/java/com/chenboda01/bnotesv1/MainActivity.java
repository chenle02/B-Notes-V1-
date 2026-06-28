package com.chenboda01.bnotesv1;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.JavascriptInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

public class MainActivity extends Activity {
    private WebView webView;

    public class AndroidBridge {
        @JavascriptInterface
        public void openBMap() {
            runOnUiThread(() -> {
                try {
                    PackageManager pm = getPackageManager();
                    Intent launch = pm.getLaunchIntentForPackage("com.chenboda01.bmapv2osflow96");
                    if (launch == null) {
                        launch = new Intent(Intent.ACTION_MAIN);
                        launch.addCategory(Intent.CATEGORY_LAUNCHER);
                        launch.setClassName("com.chenboda01.bmapv2osflow96", "com.chenboda01.bmapv2osflow96.MainActivity");
                    }
                    launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(launch);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "B-Map V1 is not installed yet.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String js = "(function(){" +
                        "window.deleteNote=function(){" +
                        "var n=cur&&cur();" +
                        "if(!n){alert(window.bnotesT?bnotesT('selectNote'):'Select a note first.');return;}" +
                        "var name=n.title||'Untitled note';" +
                        "notes=notes.filter(function(x){return x.id!==n.id});" +
                        "currentId=notes[0]?notes[0].id:null;" +
                        "saveStore();renderList();renderEditor();" +
                        "alert((window.bnotesT?bnotesT('deleted'):'Deleted ')+name);" +
                        "};" +
                        "window.bnotesLiveSave=function(){" +
                        "var n=cur&&cur();if(!n)return;" +
                        "var t=document.getElementById('title');" +
                        "var g=document.getElementById('tags');" +
                        "var b=document.getElementById('body');" +
                        "if(t)n.title=t.value||'Untitled note';" +
                        "if(g)n.tags=g.value||'';" +
                        "if(b)n.body=b.value||'';" +
                        "n.updated=now();saveStore();renderList();" +
                        "};" +
                        "window.bnotesAttach=function(){['title','tags','body'].forEach(function(id){var el=document.getElementById(id);if(el&&!el.dataset.live){el.dataset.live='1';el.addEventListener('input',window.bnotesLiveSave);}});};" +
                        "window.bnotesLang=localStorage.getItem('bnotes_lang')||'en';" +
                        "window.bnotesText={en:{sub:'Local notes, tags, favorites',map:'🗺️ B-Map V1',search:'Search notes, tags, ideas...',all:'All',fav:'Favorites',today:'Today',newBtn:'+ New',save:'Save',favorite:'★ Favorite',copy:'Copy MD',del:'Delete',titlePH:'Title',tagsPH:'Tags: math travel ideas',bodyPH:'Write your note here...',noNotes:'No notes found.',select:'Select or create a note.',selectNote:'Select a note first.',deleted:'Deleted ',copied:'Markdown copied.',shortcut:'B-Map V1 shortcut works in the Android app.',saved:'Saved. Use Android back again to leave.',lang:'Language'},zh:{sub:'本地笔记、标签、收藏',map:'🗺️ 打开 B-Map V1',search:'搜索笔记、标签、想法...',all:'全部',fav:'收藏',today:'今天',newBtn:'+ 新建',save:'保存',favorite:'★ 收藏',copy:'复制 MD',del:'删除',titlePH:'标题',tagsPH:'标签：数学 旅行 想法',bodyPH:'在这里写笔记...',noNotes:'没有找到笔记。',select:'选择或新建一条笔记。',selectNote:'请先选择一条笔记。',deleted:'已删除 ',copied:'Markdown 已复制。',shortcut:'B-Map V1 快捷方式在 Android app 中可用。',saved:'已保存。再次按返回键退出。',lang:'语言'}};" +
                        "window.bnotesT=function(k){return (bnotesText[bnotesLang]&&bnotesText[bnotesLang][k])||bnotesText.en[k]||k};" +
                        "window.bnotesApplyLang=function(){" +
                        "var t=bnotesT;" +
                        "document.querySelector('.sub').textContent=t('sub');" +
                        "var mapBtn=document.querySelector('.top .btn');if(mapBtn)mapBtn.textContent=t('map');" +
                        "var s=document.getElementById('search');if(s)s.placeholder=t('search');" +
                        "var all=document.getElementById('all');if(all)all.textContent=t('all');" +
                        "var fav=document.getElementById('fav');if(fav)fav.textContent=t('fav');" +
                        "var today=document.getElementById('today');if(today)today.textContent=t('today');" +
                        "var bs=document.querySelectorAll('.actions button');if(bs.length>=5){bs[0].textContent=t('newBtn');bs[1].textContent=t('save');bs[2].textContent=t('favorite');bs[3].textContent=t('copy');bs[4].textContent=t('del');}" +
                        "var title=document.getElementById('title');if(title)title.placeholder=t('titlePH');" +
                        "var tags=document.getElementById('tags');if(tags)tags.placeholder=t('tagsPH');" +
                        "var body=document.getElementById('body');if(body)body.placeholder=t('bodyPH');" +
                        "var sel=document.getElementById('bnotesLangSelect');if(sel)sel.value=bnotesLang;" +
                        "};" +
                        "window.bnotesSetupLang=function(){" +
                        "if(document.getElementById('bnotesLangSelect'))return;" +
                        "var top=document.querySelector('.top');if(!top)return;" +
                        "var wrap=document.createElement('div');wrap.style.display='flex';wrap.style.alignItems='center';wrap.style.gap='5px';wrap.style.marginLeft='4px';" +
                        "var lab=document.createElement('span');lab.id='bnotesLangLabel';lab.style.fontSize='12px';lab.style.color='#a8c7d9';lab.textContent=bnotesT('lang');" +
                        "var sel=document.createElement('select');sel.id='bnotesLangSelect';sel.style.border='0';sel.style.borderRadius='999px';sel.style.background='rgba(255,255,255,.14)';sel.style.color='white';sel.style.fontWeight='800';sel.style.padding='8px';sel.innerHTML='<option value=\\\"en\\\">EN</option><option value=\\\"zh\\\">中文</option>';sel.value=bnotesLang;sel.onchange=function(){bnotesLang=this.value;localStorage.setItem('bnotes_lang',bnotesLang);renderList();renderEditor();setTimeout(function(){bnotesApplyLang();document.getElementById('bnotesLangLabel').textContent=bnotesT('lang')},30);};" +
                        "wrap.appendChild(lab);wrap.appendChild(sel);top.appendChild(wrap);" +
                        "};" +
                        "var oldRenderList=window.renderList;window.renderList=function(){oldRenderList();var l=document.getElementById('list');if(l&&l.textContent.indexOf('No notes found')>=0)l.innerHTML='<div style=\\\"color:#a8c7d9;padding:20px\\\">'+bnotesT('noNotes')+'</div>';setTimeout(bnotesApplyLang,20);};" +
                        "var oldRenderEditor=window.renderEditor;" +
                        "window.renderEditor=function(){oldRenderEditor();setTimeout(function(){bnotesAttach();bnotesApplyLang();var e=document.getElementById('editor');if(e&&e.textContent.indexOf('Select or create')>=0)e.innerHTML='<div style=\\\"color:#a8c7d9;padding:20px\\\">'+bnotesT('select')+'</div>';},30);};" +
                        "var oldCopy=window.copyMD;window.copyMD=function(){var n=cur&&cur();if(!n)return;saveNote();var text='# '+n.title+'\\n\\nTags: '+n.tags+'\\n\\n'+n.body;navigator.clipboard&&navigator.clipboard.writeText(text).then(function(){alert(bnotesT('copied'))},function(){alert(text)});};" +
                        "window.openBMap=function(){if(window.AndroidBridge){AndroidBridge.openBMap()}else alert(bnotesT('shortcut'))};" +
                        "window.bnotesBack=function(){saveNote();alert(bnotesT('saved'))};" +
                        "window.bnotesAttach=function(){['title','tags','body'].forEach(function(id){var el=document.getElementById(id);if(el&&!el.dataset.live){el.dataset.live='1';el.addEventListener('input',window.bnotesLiveSave);}});};" +
                        "bnotesSetupLang();bnotesApplyLang();setTimeout(function(){renderList();renderEditor();bnotesApplyLang();},50);" +
                        "})();";
                view.evaluateJavascript(js, null);
            }
        });

        webView.addJavascriptInterface(new AndroidBridge(), "AndroidBridge");
        webView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public void onBackPressed() {
        webView.evaluateJavascript("window.bnotesBack && window.bnotesBack()", null);
    }
}
