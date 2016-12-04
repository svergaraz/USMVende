package cl.telematica.android.usmvende.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cl.telematica.android.usmvende.Models.Producto;
import cl.telematica.android.usmvende.R;


public class MyAdapterComprador extends RecyclerView.Adapter<MyAdapterComprador.ViewHolder> {

    private List<Producto> producto;
    private Activity activity;

    public MyAdapterComprador(Activity activity, List<Producto> producto){
        this.producto = producto;
        this.activity = activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //CardView cv;
        private TextView productName;
        private TextView productDesc;
        private TextView productPrecio;
        private View container;

        public ViewHolder (View itemView){
            super(itemView);
            // cv = (CardView)itemView.findViewById(R.id.cv);
            container = itemView.findViewById(R.id.cv);
            productName = (TextView)itemView.findViewById(R.id.product_name);
            //productDesc = (TextView)itemView.findViewById(R.id.product_description);
            productPrecio =(TextView)itemView.findViewById(R.id.product_precio);
        }
    }

    @Override
    public int getItemCount() {return producto.size();}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_view_comprador, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Producto produc = producto.get(position);
        holder.productName.setText(produc.getNombreP());
       // holder.productDesc.setText(produc.getDescripcion());
        holder.productPrecio.setText(produc.getPrecio());
        holder.container.setOnClickListener(onClickListener(position));

    }

    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.conteiner_cell);
                dialog.setTitle("Vendedor" + position);
                dialog.setCancelable(true);

                TextView productName = (TextView) dialog.findViewById(R.id.product_name2);
                TextView productDesc = (TextView) dialog.findViewById(R.id.product_description2);
                TextView productPrecio = (TextView) dialog.findViewById(R.id.product_precio2);

                View btnFav = dialog.findViewById(R.id.btn_ok);

                productName.setText(producto.get(position).getNombreP());
                productPrecio.setText(producto.get(position).getPrecio());
                productDesc.setText(producto.get(position).getDescripcion());

                btnFav.setOnClickListener(onFavoriteListener(position));
                dialog.show();
            }
        };
    }

    private View.OnClickListener onFavoriteListener(final int position){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                producto.get(position).setFav("SI");
                Toast.makeText(activity, producto.get(position).getFav(), Toast.LENGTH_SHORT).show();
            }


        };
    }
   /* @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }*/
}
