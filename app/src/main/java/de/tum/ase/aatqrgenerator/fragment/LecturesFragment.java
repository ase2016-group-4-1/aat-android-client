package de.tum.ase.aatqrgenerator.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.tum.ase.aatqrgenerator.R;
import de.tum.ase.aatqrgenerator.activity.MainActivity;
import de.tum.ase.aatqrgenerator.adapter.LectureListAdapter;
import de.tum.ase.aatqrgenerator.model.ExerciseGroup;
import de.tum.ase.aatqrgenerator.model.Lecture;
import de.tum.ase.aatqrgenerator.model.Session;
import de.tum.ase.aatqrgenerator.service.ApiService;
import de.tum.ase.aatqrgenerator.service.UserService;

public class LecturesFragment extends Fragment {

    private ListView lectureListView;
    private LectureListAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;

    private ApiService apiService;

    private ProgressDialog progress;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiService = new ApiService();
        apiService.setLectureListener(new ApiService.ApiLecturesListener() {
               @Override
               public void gotLectures(final List<Lecture> lectures) {
                   if(getActivity() != null) {
                       getActivity().runOnUiThread(new Runnable() {
                           @Override
                           public void run() {

                               if(progress != null && progress.isShowing()) progress.dismiss();
                               if(swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing())
                                   swipeRefreshLayout.setRefreshing(false);

                               adapter.clear();
                               adapter.addAll(lectures);
                               adapter.notifyDataSetChanged();
                           }
                       });
                   }
               }
           }
        );

        apiService.setAttendanceListener(new ApiService.ApiAttendanceListener() {
            @Override
            public void attendanceCreated(String verificationToken) {
                Log.d("LecturesFragment", "attendance created with verification token: " + verificationToken);
                if(getActivity() instanceof MainActivity){
                    ((MainActivity) getActivity()).showQr(verificationToken);
                }
            }

            @Override
            public void notSignedIn() {
                if(progress != null && progress.isShowing()) progress.dismiss();
                if(swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                Log.d("LectureFragment", "Not signed in");
            }
        });

        reload();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lecture, container, false);
        adapter = new LectureListAdapter(getActivity(), R.layout.lecture_item, new ArrayList<Lecture>());

        lectureListView = (ListView) view.findViewById(R.id.lecture_list_view);
        lectureListView.setAdapter(adapter);
        lectureListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean hasActiveSession = false;
                boolean isEnrolled = false;
                Session activeSession = null;
                for(ExerciseGroup group : adapter.getItem(position).exerciseGroups){
                    if(group.enrolled) isEnrolled = true;
                }
                for(Session session : adapter.getItem(position).sessions){
                    if(session.active){
                        hasActiveSession = true;
                        activeSession = session;
                    }
                }
                if(hasActiveSession && isEnrolled){
                    if(activeSession.attendance.status.contentEquals("none")) {
                        Log.d("LecturesFragment", "creating attendance");
                        apiService.attend(activeSession.attendanceUrl);
                    } else {
                        Log.d("LecturesFragment", "attendance already exists with verification token: " + activeSession.attendance.verificationToken);
                        if(getActivity() instanceof MainActivity){
                            ((MainActivity) getActivity()).showQr(activeSession.attendance.verificationToken);
                        }
                    }
                } else {
                    String url = "https://ase2016-group-4-1.appspot.com" + adapter.getItem(position).href;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LecturesFragment.this.reload();
            }
        });

        return view;
    }

    public void reload() {
        if(UserService.currentAccount != null){
            apiService.setToken(UserService.currentAccount.getIdToken());
            if(swipeRefreshLayout == null || !swipeRefreshLayout.isRefreshing()) {
                progress = ProgressDialog.show(getActivity(), "Lectures",
                        "Loading available lectures, please wait...", true);
            }
            apiService.getLectures();
        } else {
            if(swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
            //TODO sign in again?
            Toast.makeText(getActivity(), "User not signed in", Toast.LENGTH_SHORT).show();
            Log.d("LectureFragment", "User not signed in");
        }
    }
}
