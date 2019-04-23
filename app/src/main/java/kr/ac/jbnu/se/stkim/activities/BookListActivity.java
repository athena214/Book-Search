package kr.ac.jbnu.se.stkim.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.FirebaseApp;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.ac.jbnu.se.stkim.R;
import kr.ac.jbnu.se.stkim.adapters.BookAdapter;
import kr.ac.jbnu.se.stkim.models.Book;
import kr.ac.jbnu.se.stkim.net.BookClient;


public class BookListActivity extends AppCompatActivity {
    public static final String BOOK_DETAIL_KEY = "book";
    private ListView lvBooks;
    private BookAdapter bookAdapter;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {    // 액티비티를 생성하는 기본적인 코드
        super.onCreate(savedInstanceState);                 // {} -> 액티비티가 실행될 때 수행해야 하는 작업
        setContentView(R.layout.activity_book_list);
        FirebaseApp.initializeApp(this);
        lvBooks = (ListView) findViewById(R.id.lvBooks);
        ArrayList<Book> aBooks = new ArrayList<Book>();
        // initialize the adapter
        bookAdapter = new BookAdapter(this, aBooks);
        // attach the adapter to the ListView
        lvBooks.setAdapter(bookAdapter);
        progress = (ProgressBar) findViewById(R.id.progress);
        setupBookSelectedListener();

        // setValue 쓰기
//        User model = new User();
//        model.setId("athena");
//        model.setName("현명");
//        model.setPw("123");
//
//        FirebaseDatabase.getInstance()
//                .getReference("a")
//                .child("b")
//                .child("name")
//                .setValue("현웅");

////      getValue 읽기
////        FirebaseDatabase.getInstance()
////                .getReference("a")
////                .child("b")
////                .addListenerForSingleValueEvent(new ValueEventListener() {
////                    @Override
////                    public void onDataChange(DataSnapshot dataSnapshot) {
////                        User model = dataSnapshot.getValue(User.class);
////                        Toast.makeText(BookListActivity.this, model.getPw(), Toast.LENGTH_LONG).show();
////
////                    }
////
////                    @Override
////                    public void onCancelled(DatabaseError databaseError) {
////                        Toast.makeText(BookListActivity.this, "서버 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
//                    }
//                });

    }

    public void setupBookSelectedListener() {
        lvBooks.setOnItemClickListener((parent, view, position, id) -> {
            // Launch the detail view passing book as an extra
            Intent intent = new Intent(BookListActivity.this, BookDetailActivity.class);
            intent.putExtra(BOOK_DETAIL_KEY, bookAdapter.getItem(position));
            startActivity(intent);
        });
    }

    // Executes an API call to the OpenLibrary search endpoint, parses the results
    // Converts them into an array of book objects and adds them to the adapter
    private void fetchBooks(String query) {
        // Show progress bar before making network request
        progress.setVisibility(ProgressBar.VISIBLE);
        BookClient client = new BookClient();
        client.getBooks(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // hide progress bar
                    progress.setVisibility(ProgressBar.GONE);
                    JSONArray docs = null;
                    if (response != null) {
                        // Get the docs json array

                        Log.d("tag", "result:" + response.toString());
                        docs = response.getJSONArray("documents");

                        // Parse json array into array of model objects
                        final ArrayList<Book> books = Book.fromJson(docs);
                        // Remove all books from the adapter
                        bookAdapter.clear();

                        // Load model objects into the adapter
                        for (Book book : books) {
                            bookAdapter.add(book); // add book through the adapter
                        }
                        bookAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    Log.d("tag", "result:" + response.toString(), e);
                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.d("tag", "result:" + response.toString(), throwable);

                progress.setVisibility(ProgressBar.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_list, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Fetch the data remotely
                fetchBooks(query);
                // Reset SearchView
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchItem.collapseActionView();
                // Set activity title to search query
                BookListActivity.this.setTitle(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
