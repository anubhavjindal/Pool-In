package com.aanyajindal.pool_in;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aanyajindal.pool_in.models.Comment;
import com.aanyajindal.pool_in.models.Item;
import com.aanyajindal.pool_in.models.Post;
import com.aanyajindal.pool_in.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DiscussionFragment extends Fragment {

    private static final String TAG = "DiscussionFragment";

    DatabaseReference postsRef;
    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

    ArrayList<Comment> list = new ArrayList<>();

    public DiscussionFragment() {
        // Required empty public constructor
    }

    public static DiscussionFragment newInstance(Post post) {

        Bundle args = new Bundle();

        args.putString("title", post.getTitle());
        args.putString("date", post.getDate());
        args.putString("author", post.getAuthorId());
        args.putString("authorname", post.getAuthor());
        args.putString("body", post.getBody());
        args.putString("tags", post.getTags());
        args.putString("category", post.getCategory());
        args.putString("postid", post.getPostid());

        DiscussionFragment fragment = new DiscussionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    User user;
    ListView commentsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_discussion, container, false);

        fUser = FirebaseAuth.getInstance().getCurrentUser();


        postsRef = FirebaseDatabase.getInstance().getReference().child("posts");
        Bundle bundle = getArguments();
        final Post post = new Post(bundle.getString("title"), bundle.getString("date"), bundle.getString("body"), bundle.getString("author"), bundle.getString("authorname"), bundle.getString("tags"), bundle.getString("category"), bundle.getString("postid"));
        commentsList = (ListView) rootView.findViewById(R.id.discussion_comment_listView);

        String postid = post.getPostid();
        TextView headerTitle = (TextView) rootView.findViewById(R.id.discussion_header);
        headerTitle.setText(post.getTitle());

        final DatabaseReference commentRef = postsRef.child(postid).child("comments");
        commentsList.setAdapter(new CommentAdapter(new ArrayList<Comment>()));
        commentRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                list.add(comment);
                CommentAdapter comAdapter = new CommentAdapter(list);
                commentsList.setAdapter(comAdapter);
                Log.d(TAG, "onChildAdded: " + "I am here");

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        DatabaseReference temp = FirebaseDatabase.getInstance().getReference().child("users").child(post.getAuthorId());
//        temp.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                user = dataSnapshot.getValue(User.class);
//                discussionAuthorView.setText(user.getName());
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });


//        TextView discussionTitleView = (TextView) rootView.findViewById(R.id.discussion_title_value);
//        TextView discussionAuthorView = (TextView) rootView.findViewById(R.id.discussion_author_value);
//        TextView discussionDateView = (TextView) rootView.findViewById(R.id.discussion_date_value);
//        TextView discussionBodyView = (TextView) rootView.findViewById(R.id.discussion_body_value);
//
//        Button addCommentButton = (Button) rootView.findViewById(R.id.btn_addComment);


        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.discussion_list, commentsList, false);
        TextView discussionAuthorView = (TextView) header.findViewById(R.id.discussion_author_value);
        TextView discussionDateView = (TextView) header.findViewById(R.id.discussion_date_value);
        TextView discussionBodyView = (TextView) header.findViewById(R.id.discussion_body_value);
        final EditText messageBox = (EditText) rootView.findViewById(R.id.message_box);
        ImageButton addCommentButton = (ImageButton) rootView.findViewById(R.id.btn_addComment);
        commentsList.addHeaderView(header, null, false);


        discussionAuthorView.setText(post.getAuthor());

        discussionAuthorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("userid", post.getAuthorId());
                startActivity(intent);
            }
        });


        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat fmt2 = new SimpleDateFormat("EEE, MMM d, ''yy");
        String frDate = "";
        try {
            Date date = fmt.parse(post.getDate());
            frDate = fmt2.format(date);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        discussionDateView.setText(frDate);

        discussionBodyView.setText(post.getBody());

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LayoutInflater li = LayoutInflater.from(getActivity());
//                final View addCommentDialogView = li.inflate(R.layout.add_comment_dialog, null);
//                final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
//                final EditText etAddComment = (EditText) addCommentDialogView.findViewById(R.id.et_addComment);
//                alert.setView(addCommentDialogView);
//                alert.setTitle("Add Comment");
//                alert.setPositiveButton("Add Comment", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String body = etAddComment.getText().toString();
//                        Date newDate = new Date();
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//                        String date = sdf.format(newDate);
//                        Comment comment = new Comment(fUser.getUid(), fUser.getDisplayName(), date, body);
//                        commentRef.push().setValue(comment);
//                    }
//                });
//                alert.setNegativeButton("CANCEL", null);
//                alert.create();
//                alert.show();

                String body = messageBox.getText().toString();
                Date newDate = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String date = sdf.format(newDate);
                Comment comment = new Comment(fUser.getUid(), fUser.getDisplayName(), date, body);
                commentRef.push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Comment added! :)", Toast.LENGTH_SHORT).show();
                        //TODO: improve this thing abhi khaas nahi hai
                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        //inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                        inputMethodManager.hideSoftInputFromWindow(
                                getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        messageBox.setText("");


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Comment could not be added :(", Toast.LENGTH_SHORT).show();
                    }
                });

                //TODO: add kind of on complete listener after comment is pushed to empty the edit text use imeoptions to hide edit text
                //http://stackoverflow.com/questions/19217582/implicit-submit-after-hitting-done-on-the-keyboard-at-the-last-edittext

            }
        });


        return rootView;
    }

    class CommentAdapter extends BaseAdapter {
        class Holder {
            TextView name;
            TextView comment;
            TextView date;
        }

        ArrayList<Comment> mList;

        public CommentAdapter(ArrayList<Comment> mList) {
            this.mList = mList;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0)
                return 0;
            else
                return 1;
        }


        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater li = LayoutInflater.from(getActivity());
            Holder holder = new Holder();
            if (convertView == null) {
                convertView = li.inflate(R.layout.comment_list, null);

                holder.name = (TextView) convertView.findViewById(R.id.tv_list_author);
                holder.comment = (TextView) convertView.findViewById(R.id.tv_list_body);
                holder.date = (TextView) convertView.findViewById(R.id.tv_list_date);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            Comment comment = (Comment) getItem(position);

            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat fmt2 = new SimpleDateFormat("EEE, MMM d, ''yy");
            String frDate = "";
            try {
                Date date = fmt.parse(comment.getDate().toString());
                frDate = fmt2.format(date);
            } catch (ParseException pe) {
                pe.printStackTrace();
            }
            holder.date.setText(frDate);

            holder.comment.setText(comment.getBody());
            holder.name.setText(comment.getAuthor());


//            DatabaseReference temp = FirebaseDatabase.getInstance().getReference().child("users").child(comment.getAuthorId());
//            final Holder finalHolder = holder;
//            temp.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    user = dataSnapshot.getValue(User.class);
//                    finalHolder.name.setText(user.getName());
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
            return convertView;
        }


    }
}