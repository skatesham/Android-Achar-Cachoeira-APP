package fiorin.sham.aventura.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import fiorin.sham.aventura.Model.Banco;
import fiorin.sham.aventura.Model.EnumPapeis;
import fiorin.sham.aventura.Model.Papel;
import fiorin.sham.aventura.R;

/**
 * Created by sham on 21/11/17.
 */

public class OpcoesFragment extends Fragment implements View.OnClickListener{

    private View myView;
    private Banco banco;
    private Button analisarImagensButton;
    private TextView desenvolvedorTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        myView = inflater.inflate(R.layout.opcoes_layout, container, false);
        banco = Banco.getInstance();
        analisarImagensButton = (Button) myView.findViewById(R.id.analisarImagens);
        desenvolvedorTextView = (TextView) myView.findViewById(R.id.desenvolvedorTextView);
        analisarImagensButton.setVisibility(View.GONE);
        analisarImagensButton.setOnClickListener(this);
        desenvolvedorTextView.setOnClickListener(this);
        return myView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.analisarImagens:
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new ValidationFragment()).addToBackStack(null).commit();
                break;
            case R.id.desenvolvedorTextView:
                if (banco.getUsuario() != null) {
                    for (Papel p : banco.getUsuario().getPapeis()) {
                        if (p.getDescricao() == EnumPapeis.ADMINISTRADOR) {
                            analisarImagensButton.setVisibility(View.VISIBLE);

                        }
                    }
                }else {
                    Toast.makeText(getActivity(), "Você não possui privilégios de Administrador", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

