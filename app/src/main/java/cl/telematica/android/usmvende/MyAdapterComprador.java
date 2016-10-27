package cl.telematica.android.usmvende;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by simon on 27-10-2016.
 */

public class MyAdapterComprador extends RecyclerView.Adapter<MyAdapterComprador.ViewHolder> {

    private List<Producto> producto;

    public MyAdapterComprador(List<Producto> producto){this.producto = producto;}

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView productName;
        TextView productDesc;
        TextView productPrecio;

        public ViewHolder (View itemView){
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            productName = (TextView)itemView.findViewById(R.id.product_name);
            productDesc = (TextView)itemView.findViewById(R.id.product_description);
            productPrecio =(TextView)itemView.findViewById(R.id.product_precio);
        }
    }

    @Override
    public int getItemCount() {return producto.size();}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_comprador,
                                                                  parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.productName.setText(producto.get(position).getNombreP());
        holder.productDesc.setText(producto.get(position).getDescripcion());
        holder.productPrecio.setText(producto.get(position).getPrecio());
    }


}
