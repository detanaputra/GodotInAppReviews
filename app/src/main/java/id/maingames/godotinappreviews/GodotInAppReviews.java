package id.maingames.godotinappreviews;

import android.util.ArraySet;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;

import java.util.Set;

public class GodotInAppReviews extends GodotPlugin {
    private static String TAG = "GodotInAppReviews";
    ReviewManager manager;
    ReviewInfo reviewInfo;

    public GodotInAppReviews(Godot godot) {
        super(godot);
        TAG = getPluginName();
    }

    @UsedByGodot
    public void requestReviewInfo(){
        manager = ReviewManagerFactory.create(getActivity());
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(getActivity(), new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(@NonNull Task<ReviewInfo> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "Request review info flow has succeed");
                    reviewInfo = task.getResult();
                    emitSignal("_request_reviewinfo_completed", (Boolean)true);
                }
                else{
                    Log.e(TAG, "Request review info flow has failed: " + task.getException().getLocalizedMessage(), task.getException());
                    emitSignal("_request_reviewinfo_completed", (Boolean)false);
                }
            }
        });
    }

    @UsedByGodot
    public void launchReviewFlow(){
        Log.d(TAG, "Launching review flow..");
        try{
            manager.launchReviewFlow(getActivity(), reviewInfo)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "Launching Review flow has succeed");
                            emitSignal("_launch_reviewflow_completed", (Boolean)true);
                        }
                        else{
                            Log.e(TAG, "Launching Review flow has failed: " + task.getException().getLocalizedMessage(), task.getException());
                            emitSignal("_launch_reviewflow_completed", (Boolean)false);
                        }
                    }
                });
        } catch (Exception e){
            Log.e(TAG, "Launching Review flow has failed: " + e.getLocalizedMessage(), e);
            emitSignal("_launch_reviewflow_completed", (Boolean)false);
        }

    }

    @NonNull
    @Override
    public String getPluginName() {
        return "GodotInAppReviews";
    }

    @Override
    public Set<SignalInfo> getPluginSignals(){
        Set<SignalInfo> signals = new ArraySet<>();

        signals.add(new SignalInfo("_request_reviewinfo_completed", Boolean.class));
        signals.add(new SignalInfo("_launch_reviewflow_completed", Boolean.class));
        return signals;
    }
}
