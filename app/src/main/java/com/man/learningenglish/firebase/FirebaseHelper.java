package com.man.learningenglish.firebase;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.man.learningenglish.models.Leaderboard;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper {
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final LeaderboardCallback callback;

    public static List<Leaderboard> leaderboardList = new ArrayList<>(); // Initialize here
    public static Leaderboard myLeaderboard = new Leaderboard();
    public static boolean isMeOnLeaderboard = false;

    public interface LeaderboardCallback {
        void onLeaderboardUpdated();
        void onError(Exception e);
    }

    public FirebaseHelper(LeaderboardCallback callback) {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.callback = callback;
    }

    public void getTopUsers() {
        String myUID = FirebaseAuth.getInstance().getUid();

        db.collection("leaderboard")
                .orderBy("quickestTime")
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    leaderboardList.clear();
                    int rank = 1;
                    isMeOnLeaderboard = false;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Leaderboard leaderboard = new Leaderboard();
                        leaderboard.setTime(doc.getLong("quickestTime"));
                        leaderboard.setName(doc.getString("name"));
                        leaderboard.setRank(rank);
                        leaderboard.setUid(doc.getId());
                        leaderboardList.add(leaderboard);

                        if (myUID.equals(doc.getId())) {
                            isMeOnLeaderboard = true;
                            myLeaderboard = leaderboard;
                        }

                        rank++;
                    }

                    if (!isMeOnLeaderboard && myUID != null) {
                        getUserRank(myUID);
                    } else {
                        callback.onLeaderboardUpdated();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseHelper", "Error getting documents: ", e);
                    callback.onError(e);
                });
    }

    private void getUserRank(String uid) {
        db.collection("leaderboard")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        myLeaderboard.setTime(documentSnapshot.getLong("quickestTime"));
                        myLeaderboard.setName(documentSnapshot.getString("name"));
                        myLeaderboard.setUid(documentSnapshot.getId());
                        if (myLeaderboard != null) {
                            db.collection("leaderboard")
                                    .whereLessThan("time", myLeaderboard.getTime())
                                    .get()
                                    .addOnSuccessListener(betterTimesSnapshot -> {
                                        myLeaderboard.setRank(betterTimesSnapshot.size() + 1);
                                        callback.onLeaderboardUpdated();
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> callback.onError(e));
    }
}