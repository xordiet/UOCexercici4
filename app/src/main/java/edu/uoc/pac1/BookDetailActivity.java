package edu.uoc.pac1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.List;
import java.util.Set;

/**
 * An activity representing a single Book detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link BookListActivity}.
 */
public class BookDetailActivity extends AppCompatActivity {

    private final static String TAG = "BookDetalilActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebView myWebView = (WebView) findViewById(R.id.webview);
                myWebView.setVisibility(View.VISIBLE);
                myWebView.setWebViewClient(new MyWebViewClient());
                myWebView.loadUrl("file:///android_asset/form.html");
                findViewById(R.id.fab).setVisibility(View.GONE);

            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            showDetail(0);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String dades = new String(Uri.parse(url).getQuery().toString());
            String[] args = dades.split("&");
            String[] nom = args[0].split("=");
            String[] num = args[1].split("=");
            String[] data = args[2].split("=");
            if(nom.length==1){
                Snackbar.make(view, "has d'emplenar el camp 'Nom'", Snackbar.LENGTH_LONG)
                        .setAction("mesInfo", null).show();
            } else if(num.length==1){
                Snackbar.make(view, "has d'emplenar el camp 'Número tarjeta'", Snackbar.LENGTH_LONG)
                        .setAction("mesInfo", null).show();
            }else if(data.length==1){
                Snackbar.make(view, "has d'emplenar el camp 'Data caducitat'", Snackbar.LENGTH_LONG)
                        .setAction("mesInfo", null).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(BookDetailActivity.this);
                builder.setMessage("La teva compra s'ha realitzat correctament")
                        .setPositiveButton("iupi!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                findViewById(R.id.fab).setVisibility(View.VISIBLE);
                                findViewById(R.id.webview).setVisibility(View.GONE);
                            }
                        })
                        .show();

            }
            return true;
        }
    }


    private void showDetail(int pos) {
        Bundle arguments = new Bundle();
        arguments.putInt(BookDetailFragment.ARG_ITEM_ID,
                getIntent().getIntExtra(BookDetailFragment.ARG_ITEM_ID, pos));
        BookDetailFragment fragment = new BookDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.book_detail_container, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, BookListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
