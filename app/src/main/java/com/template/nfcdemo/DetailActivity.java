package com.template.nfcdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "XXXXXXXX";

    ImageView imageView;
    TextView tvname, tvwei, tvhei, tvabi;

    NfcAdapter mNfcAdapter;
    PendingIntent mNfcPendingIntent;
    IntentFilter[] mNdefExchangeFilters;
    Pokemon mPokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initAndGetDataIntent();
        initNfc();

    }

    void initAndGetDataIntent() {
        imageView = findViewById(R.id.image);
        tvname = findViewById(R.id.name);
        tvwei = findViewById(R.id.weight);
        tvhei = findViewById(R.id.height);
        tvabi = findViewById(R.id.ability);

        if (getIntent() != null && getIntent().getExtras() != null) {
            mPokemon = (Pokemon) getIntent().getExtras().getSerializable("image");
            if (mPokemon != null) {
                fetchPokemonInfo(mPokemon);
            }
        }
    }

    void fetchPokemonInfo(Pokemon pokemon){
        Glide.with(this).load(pokemon.image).into(imageView);
        tvname.setText(pokemon.name);
        tvwei.setText(pokemon.weight);
        tvhei.setText(pokemon.height);
        tvabi.setText(pokemon.ability);
    }

    void initNfc() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Handle all of our received NFC intents in this activity.
        mNfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Intent filters for reading a note from a tag or exchanging over p2p.
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefDetected.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
        }
        mNdefExchangeFilters = new IntentFilter[]{ndefDetected};


    }

    @Override
    protected void onResume() {
        super.onResume();
        // Enable to send data (push this message to the other device)
        mNfcAdapter.enableForegroundNdefPush(DetailActivity.this, getNoteAsNdef());
        // Enable to receive data (set up the listener for the intent that we are filtering for such that when it detects an intent matching the intent filter, which invokes our activity’s onNewIntent method)
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mNdefExchangeFilters, null);

        // TBD
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            setIntent(new Intent());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundNdefPush(this);
        mNfcAdapter.disableForegroundDispatch(this);
    }

    // For sending data
    private NdefMessage getNoteAsNdef() {
        if (mPokemon == null)
            return null;
        Gson gson = new Gson();
        String pokemonString = gson.toJson(mPokemon);
        Toast.makeText(DetailActivity.this, "Sender: " + pokemonString, Toast.LENGTH_SHORT).show();
        byte[] textBytes = pokemonString.getBytes();
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/plain".getBytes(),
                new byte[]{}, textBytes);
        return new NdefMessage(new NdefRecord[]{
                textRecord
        });
    }

    // For receiving data
    @Override
    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            NdefMessage[] msgs = getNdefMessages(intent);
            String body = new String(msgs[0].getRecords()[0].getPayload());
            Log.d(TAG, "onNewIntent: " + body);
            Toast.makeText(DetailActivity.this, "Receiver: " + body, Toast.LENGTH_SHORT).show();
            Gson gson = new Gson();
            try {
                Pokemon pokemon = gson.fromJson(body, Pokemon.class);
                mPokemon = pokemon;
                fetchPokemonInfo(pokemon);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }

        }
        super.onNewIntent(intent);
    }

    // For receiving data
    NdefMessage[] getNdefMessages(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[]{};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{
                        record
                });
                msgs = new NdefMessage[]{
                        msg
                };
            }
        } else {
            Log.d(TAG, "Unknown intent.");
            finish();
        }
        return msgs;
    }

}
