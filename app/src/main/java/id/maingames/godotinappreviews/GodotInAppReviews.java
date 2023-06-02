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
    private static String TAG = "";
    ReviewManager manager;
    ReviewInfo reviewInfo;

    public GodotInAppReviews(Godot godot) {
        super(godot);
        TAG = getActivity().getString(R.string.app_name);
    }

    @UsedByGodot
    public void init(){
        manager = ReviewManagerFactory.create(getActivity());
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(getActivity(), new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(@NonNull Task<ReviewInfo> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "Get request review flow has succeed");
                    reviewInfo = task.getResult();
                    emitSignal("_inappreview_initiated", true);
                }
                else{
                    Log.e(TAG, "Get request review flow has failed: " + task.getException().getLocalizedMessage(), task.getException());
                    emitSignal("_inappreview_initiated", false);
                }
            }
        });
    }

    @UsedByGodot
    public void launch(){
        Log.d(TAG, "Launching review flow..");
        manager.launchReviewFlow(getActivity(), reviewInfo)
            .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Log.d(TAG, "Launching Review flow has succeed");
                        emitSignal("_inappreview_completed", true);
                    }
                    else{
                        Log.e(TAG, "Launching Review flow has failed: " + task.getException().getLocalizedMessage(), task.getException());
                        emitSignal("_inappreview_completed", false);
                    }
                }
            });

    }

    @NonNull
    @Override
    public String getPluginName() {
        return getActivity().getString(R.string.app_name);
    }

    @Override
    public Set<SignalInfo> getPluginSignals(){
        Set<SignalInfo> signals = new ArraySet<>();

        signals.add(new SignalInfo("_inappreview_initiated", boolean.class));
        signals.add(new SignalInfo("_inappreview_completed", boolean.class));
        return signals;
    }
}
