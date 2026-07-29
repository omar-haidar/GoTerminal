package dev.omar.goterminal.ui.sheet;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import dev.omar.goterminal.databinding.SheetProgressBinding;

/**
 * A modern, thread-safe Progress BottomSheetDialog built with SOLID principles.
 * Uses ViewBinding and follows the Builder design pattern.
 */
public class ProgressSheetDialog extends BottomSheetDialogFragment {

    private SheetProgressBinding binding;
    private final DialogConfig config;

    // Private constructor to enforce usage of Builder
    private ProgressSheetDialog(DialogConfig config) {
        this.config = config;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SheetProgressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupInitialUI();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && config.onShowListener != null) {
            getDialog().setOnShowListener(config.onShowListener);
        }
    }

    private void setupInitialUI() {
        if (binding == null) return;

        // Apply configuration to UI
        updateTextVisibility(binding.sheetTitle, config.title);
        updateTextVisibility(binding.sheetMessage, config.message);

        binding.sheetProgress.setIndeterminate(config.indeterminate);
        if (!config.indeterminate) {
            binding.sheetProgress.setMax(config.max);
            binding.sheetProgress.setProgress(config.progress);
        }

        setupButton(binding.sheetPositiveBtn, config.positiveText, config.positiveListener);
        setupButton(binding.sheetNegativeBtn, config.negativeText, config.negativeListener);

        setCancelable(config.cancelable);
    }

    private void setupButton(View button, CharSequence text, View.OnClickListener listener) {
        if (button instanceof android.widget.TextView) {
            android.widget.TextView btn = (android.widget.TextView) button;
            if (!TextUtils.isEmpty(text)) {
                btn.setText(text);
                btn.setVisibility(View.VISIBLE);
                btn.setOnClickListener(v -> {
                    if (listener != null) listener.onClick(v);
                    dismiss();
                });
            } else {
                btn.setVisibility(View.GONE);
            }
        }
    }

    private void updateTextVisibility(android.widget.TextView textView, CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    // --- Public API for dynamic updates (Thread-safe) ---

    public void setProgress(int progress) {
        config.progress = progress;
        runOnUiThread(() -> {
            if (binding != null) {
                binding.sheetProgress.setIndeterminate(false);
                binding.sheetProgress.setProgress(progress);
            }
        });
    }

    public void setMax(int max) {
        config.max = max;
        runOnUiThread(() -> {
            if (binding != null) binding.sheetProgress.setMax(max);
        });
    }

    public void setMessage(CharSequence message) {
        config.message = message;
        runOnUiThread(() -> {
            if (binding != null) updateTextVisibility(binding.sheetMessage, message);
        });
    }

    public void setIndeterminate(boolean indeterminate) {
        config.indeterminate = indeterminate;
        runOnUiThread(() -> {
            if (binding != null) binding.sheetProgress.setIndeterminate(indeterminate);
        });
    }

    private void runOnUiThread(Runnable action) {
        if (binding != null) {
            binding.getRoot().post(action);
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if (config.onCancelListener != null) config.onCancelListener.onCancel(dialog);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (config.onDismissListener != null) config.onDismissListener.onDismiss(dialog);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }

    @Override
    public void dismiss() {
        new Handler(Looper.getMainLooper()).post(()->{
            super.dismiss();
        });

    }

    /**
     * Configuration class to hold dialog state (SRP).
     */
    private static class DialogConfig {
        CharSequence title, message;
        int max = 100, progress = 0;
        boolean indeterminate = true;
        CharSequence positiveText, negativeText;
        View.OnClickListener positiveListener, negativeListener;
        boolean cancelable = true;
        DialogInterface.OnShowListener onShowListener;
        DialogInterface.OnDismissListener onDismissListener;
        DialogInterface.OnCancelListener onCancelListener;
    }

    /**
     * Modern Builder for ProgressSheetDialog.
     */
    public static class Builder {
        private final DialogConfig config = new DialogConfig();

        public Builder setTitle(CharSequence title) {
            config.title = title;
            return this;
        }

        public Builder setMessage(CharSequence message) {
            config.message = message;
            return this;
        }

        public Builder setMax(int max) {
            config.max = max;
            config.indeterminate = false;
            return this;
        }

        public Builder setProgress(int progress) {
            config.progress = progress;
            config.indeterminate = false;
            return this;
        }

        public Builder setIndeterminate(boolean indeterminate) {
            config.indeterminate = indeterminate;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            config.cancelable = cancelable;
            return this;
        }

        public Builder setPositiveButton(CharSequence text, View.OnClickListener listener) {
            config.positiveText = text;
            config.positiveListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text, View.OnClickListener listener) {
            config.negativeText = text;
            config.negativeListener = listener;
            return this;
        }

        public Builder setOnShowListener(DialogInterface.OnShowListener listener) {
            config.onShowListener = listener;
            return this;
        }

        public Builder setOnDismissListener(DialogInterface.OnDismissListener listener) {
            config.onDismissListener = listener;
            return this;
        }

        public Builder setOnCancelListener(DialogInterface.OnCancelListener listener) {
            config.onCancelListener = listener;
            return this;
        }

        @NonNull
        public ProgressSheetDialog create() {
            return new ProgressSheetDialog(config);
        }

        public ProgressSheetDialog show(@NonNull FragmentManager manager, String tag) {
            ProgressSheetDialog dialog = create();
            dialog.show(manager, tag);
            return dialog;
        }
    }
}
