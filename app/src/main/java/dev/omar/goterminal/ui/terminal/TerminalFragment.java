package dev.omar.goterminal.ui.terminal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.termux.terminal.TerminalSession;
import com.termux.view.TerminalView;

import dev.omar.goterminal.MainViewModel;
import dev.omar.goterminal.R;

public class TerminalFragment extends Fragment {

    private static final String ARG_SESSION_HANDLE = "session_handle";
    private String sessionHandle;
    private TerminalView terminalView;
    private MainViewModel viewModel;

    public static TerminalFragment newInstance(String sessionHandle) {
        TerminalFragment fragment = new TerminalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SESSION_HANDLE, sessionHandle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sessionHandle = getArguments().getString(ARG_SESSION_HANDLE);
        }
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminal, container, false);
        terminalView = view.findViewById(R.id.terminal_view);
        terminalView.setTextSize(12);
        
        terminalView.setOnClickListener(v -> {
            terminalView.requestFocus();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(terminalView, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        attachSession();
        return view;
    }

    private void attachSession() {
        if (viewModel.getSessions().getValue() != null) {
            for (TerminalSession session : viewModel.getSessions().getValue()) {
                if (session.mHandle.equals(sessionHandle)) {
                    terminalView.setTerminalViewClient(viewModel.getTerminalBackend());
                    terminalView.attachSession(session);
                    break;
                }
            }
        }
    }
    
    public String getSessionHandle() {
        return sessionHandle;
    }
}
