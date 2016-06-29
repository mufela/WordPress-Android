package org.wordpress.android.ui.publicize;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import org.wordpress.android.R;
import org.wordpress.android.models.PublicizeConnection;
import org.wordpress.android.util.ToastUtils;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class PublicizeAccountChooserDialogFragment extends DialogFragment implements PublicizeAccountChooserListAdapter.OnPublicizeAccountChooserListener {
    private RecyclerView mNotConnectedRecyclerView;
    private PublicizeConnection[] mPublicizeConnections;
    private ArrayList<PublicizeConnection> mNotConnectedAccounts;
    private ArrayList<PublicizeConnection> mConnectedAccounts;
    private String mSocialNetwork;
    private int mSelectedIndex = 0;
    private int mCurrentSite = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        retrieveCurrentSite();
        addConnectionsToLists();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.publicize_account_chooser_dialog, null);
        builder.setView(view);
        builder.setPositiveButton(R.string.share_btn_connect, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                int keychainId = mNotConnectedAccounts.get(mSelectedIndex).connectionId;
                EventBus.getDefault().post(new PublicizeEvents.ActionAccountChosen(mCurrentSite, keychainId));
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                ToastUtils.showToast(getActivity(), getActivity().getString(R.string.cannot_connect_account_error, mSocialNetwork));
            }
        });
        builder.setTitle(getString(R.string.connecting_social_network, mSocialNetwork));
        builder.setMessage(getString(R.string.connection_chooser_message));

        mNotConnectedRecyclerView = (RecyclerView) view.findViewById(R.id.not_connected_recyclerview);
        mNotConnectedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        PublicizeAccountChooserListAdapter notConnectedAdapter = new PublicizeAccountChooserListAdapter(mNotConnectedAccounts, this, false);
        notConnectedAdapter.setHasStableIds(true);
        mNotConnectedRecyclerView.setAdapter(notConnectedAdapter);

        RecyclerView listViewConnected = (RecyclerView) view.findViewById(R.id.connected_recyclerview);
        listViewConnected.setLayoutManager(new LinearLayoutManager(getContext()));
        PublicizeAccountChooserListAdapter connectedAdapter = new PublicizeAccountChooserListAdapter(mConnectedAccounts, null, true);
        listViewConnected.setAdapter(connectedAdapter);

        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity != null && activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    public void setConnections(PublicizeConnection[] publicizeConnections) {
        mPublicizeConnections = publicizeConnections;
    }

    private void addConnectionsToLists() {
        mNotConnectedAccounts = new ArrayList<>();
        mConnectedAccounts = new ArrayList<>();
        for (int i = 0; i < mPublicizeConnections.length; i++) {
            PublicizeConnection connection = mPublicizeConnections[i];
            if (containsCurrentSite(connection.getSites())) {
                mConnectedAccounts.add(connection);
            } else {
                mNotConnectedAccounts.add(connection);
            }
        }
    }

    private boolean containsCurrentSite(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == mCurrentSite) {
                return true;
            }
        }

        return false;
    }

    private void retrieveCurrentSite() {
        Bundle args = getArguments();
        if (args != null) {
            mCurrentSite = args.getInt("site_id");
            mSocialNetwork = args.getString("social_network");
        }
    }

    @Override
    public void onAccountSelected(int selectedIndex) {
        mSelectedIndex = selectedIndex;
        mNotConnectedRecyclerView.getAdapter().notifyDataSetChanged();
    }
}
