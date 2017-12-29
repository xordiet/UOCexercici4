package edu.uoc.pac1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.uoc.pac1.model.BookContent;

import static java.security.AccessController.getContext;

/**
 * An activity representing a list of Books. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link BookDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class BookListActivity extends AppCompatActivity {

    private final static String TAG = "BookListActivity";
    private final static String ACTION_DELETE = "ACTION_DELETE";
    private final static String ACTION_DETAIL = "ACTION_DETAIL";
    private final static String BOOK_POSITION = "BOOK_POSITION";
    private int bookPosition = 0;
    private String currentAction = null;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private SimpleItemRecyclerViewAdapter adapter;
    private DatabaseReference myRef;
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_list);


        // ============ INICI CODI A COMPLETAR ===============
        if (getIntent() != null && getIntent().getAction() != null && getIntent().hasExtra(BOOK_POSITION)) {
            String bookPositionS = getIntent().getStringExtra(BOOK_POSITION);
            bookPosition = Integer.valueOf(bookPositionS);
            if (getIntent().getAction().equalsIgnoreCase(ACTION_DELETE)) {
                // Acción eliminar de la notificación recibida
                currentAction = ACTION_DELETE;
            } else if (getIntent().getAction().equalsIgnoreCase(ACTION_DETAIL)) {
                // Acción reenviar de la notificación recibida
                currentAction = ACTION_DETAIL;
            } else {
                currentAction = null;
            }
            NotificationManager nMgr = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancelAll();
        } else {
            currentAction = null;
        }
        // ============ FI CODI A COMPLETAR ===============

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // ============ INICI CODI A COMPLETAR ===============
        mAuth.signInWithEmailAndPassword("jo@jordiborras.cat", "123456")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            downloadBooks();
                        } else {
                            Toast.makeText(BookListActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // ============ FI CODI A COMPLETAR ===============

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // ============ INICI CODI A COMPLETAR ===============
                refreshBookList();
                // ============ FI CODI A COMPLETAR ===============
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        recyclerView = (RecyclerView) findViewById(R.id.book_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        setupRecyclerView(recyclerView);

        if (findViewById(R.id.book_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }


        /**
         *
         *  afegir el menú lateral
         *
         */

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.el_juego_de_ender)
                .addProfiles(
                        new ProfileDrawerItem().withName("Jordi Borràs").withEmail("jo@jordiborras.cat").withIcon(getResources().getDrawable(R.drawable.ic_account_circle_black_24dp))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        SecondaryDrawerItem item1 = new SecondaryDrawerItem().withIdentifier(1).withName("Compartir amb altres apps");
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName("Copiar al portaretalls");
        SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(3).withName("Compartir al Whatsapp");

        //Now create your drawer and pass the AccountHeader.Result
        new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        new DividerDrawerItem(),
                        item3
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        int premut = ((int) drawerItem.getIdentifier());
                        //Toast.makeText(BookListActivity.this, "posició: "+premut, Toast.LENGTH_SHORT).show();
                        if(premut == 1){
                            //Toast.makeText(BookListActivity.this, "compartir apps: "+premut, Toast.LENGTH_SHORT).show();
                            compartirApps();
                        } else if (premut == 2){
                            //Toast.makeText(BookListActivity.this, "copiar portaretalls: "+premut, Toast.LENGTH_SHORT).show();
                            copiarPortaretalls();
                        } else if (premut == 3){
                            //Toast.makeText(BookListActivity.this, "compartir whats: "+premut, Toast.LENGTH_SHORT).show();
                            compartirWhats();
                        } else {
                        }
                        return true;
                    }
                })
                .build();


    }

    private void compartirApps() {
        Uri imageUri;
        imageUri = Uri.parse("android.resource://" + getPackageName() + "/mipmap/" + "ic_launcher");
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Aplicació Android sobre llibres.");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, "envia a..."));
    }

    private void copiarPortaretalls() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", "Aplicació Android sobre llibres.");
        clipboard.setPrimaryClip(clip);
        //crea el l'alerta
        DialogFragment newFragment = new MostraAlertes();
        newFragment.show(getSupportFragmentManager(), "copia");
    }

    private void compartirWhats() {
        Uri imageUri;
        imageUri = Uri.parse("android.resource://" + getPackageName() + "/mipmap/" + "ic_launcher");
        //File imagePath = new File(Context.getFilesDir(), "imatges");
        //File newFile = new File(imagePath, "defecte.jpg");
        //Uri contentUri = getUriForFile(getContext(), "edu.uoc.pac1.fileprovider", newFile);
        //imageUri = Uri.parse("content://edu.uoc.pac1.fileprovider/imatges/defecte.jpg");
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Aplicació Android sobre llibres.");
        sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        sendIntent.setType("image/*");
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);
    }

    public static class MostraAlertes extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("El text ha estat desat")
                    .setPositiveButton("Gràcies", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // No fa res
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    private void downloadBooks() {
        swipeContainer.setRefreshing(true);

        myRef = database.getReference("books");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getBooksFromDataSnapshot(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                getBooksFromDB();
            }
        });
    }


    private void refreshBookList() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getBooksFromDataSnapshot(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
                getBooksFromDB();
            }
        });
    }

    private void getBooksFromDB() {
        List<BookContent.BookItem> values = BookContent.getBooks();
        loadBooks(values);
    }

    private void getBooksFromDataSnapshot(DataSnapshot dataSnapshot) {
        // This method is called once with the initial value and again
        // whenever data at this location is updated.
        GenericTypeIndicator<ArrayList<BookContent.BookItem>> genericTypeIndicator =
                new GenericTypeIndicator<ArrayList<BookContent.BookItem>>() {};
        ArrayList<BookContent.BookItem> values = dataSnapshot.getValue(genericTypeIndicator);
        // Save data in database
        for (BookContent.BookItem bookItem : values) {
            if (!BookContent.exists(bookItem)) {
                bookItem.save();
            }
        }
        loadBooks(values);
    }

    private void loadBooks(List<BookContent.BookItem> values) {
        if (currentAction != null && currentAction.equals(ACTION_DELETE)) {
            values.remove(bookPosition);
            currentAction = null;
        }

        adapter.setItems(values);
        swipeContainer.setRefreshing(false);

        if (currentAction != null && currentAction.equals(ACTION_DETAIL)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerView.findViewHolderForAdapterPosition(bookPosition).itemView.performClick();
                }
            }, 1);
            currentAction = null;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new SimpleItemRecyclerViewAdapter(new ArrayList<BookContent.BookItem>());
        recyclerView.setAdapter(adapter);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<BookContent.BookItem> mValues;
        private final static int EVEN = 0;
        private final static int ODD = 1;

        public SimpleItemRecyclerViewAdapter(List<BookContent.BookItem> items) {
            mValues = items;
        }

        public void setItems(List<BookContent.BookItem> items) {
            // ============ INICI CODI A COMPLETAR ===============
            mValues = items;
            notifyDataSetChanged();
            // ============ FI CODI A COMPLETAR ===============
        }

        @Override
        public int getItemViewType(int position) {
            int type;
            if (position % 2 == 0) {
                type = EVEN;
            } else {
                type = ODD;
            }
            return type;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == EVEN) {
                 view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.book_list_content, parent, false);
            } else if (viewType == ODD) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.book_list_content_odd, parent, false);
            }
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            holder.mTitleView.setText(mValues.get(position).title);
            holder.mAuthorView.setText(mValues.get(position).author);

            // ============ INICI CODI A COMPLETAR ===============
            holder.mView.setTag(position);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentPos = (int) v.getTag();
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putInt(BookDetailFragment.ARG_ITEM_ID, currentPos);
                        BookDetailFragment fragment = new BookDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.book_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, BookDetailActivity.class);
                        intent.putExtra(BookDetailFragment.ARG_ITEM_ID, currentPos);
                        context.startActivity(intent);
                    }
                }
            });
            // ============ FI CODI A COMPLETAR ===============
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mTitleView;
            public final TextView mAuthorView;
            public BookContent.BookItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTitleView = (TextView) view.findViewById(R.id.title);
                mAuthorView = (TextView) view.findViewById(R.id.author);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTitleView.getText() + "'";
            }
        }
    }
}
