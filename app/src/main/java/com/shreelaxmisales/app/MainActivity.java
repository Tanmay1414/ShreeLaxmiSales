package com.shreelaxmisales.app;

import android.content.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.core.content.FileProvider;
import java.io.*;
import java.text.*;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout root, itemsBox; TextView totalView, qty, rate, billPreview; EditText name;
    ArrayList<Item> items=new ArrayList<>(); String active="qty"; SharedPreferences prefs;
    int blue=Color.rgb(21,101,192), green=Color.rgb(46,125,50), dark=Color.rgb(35,35,35);
    @Override public void onCreate(Bundle b){super.onCreate(b); prefs=getSharedPreferences("bills",0); showEntry();}
    TextView tv(String s,int sp){ TextView t=new TextView(this); t.setText(s); t.setTextSize(sp); t.setTextColor(dark); t.setPadding(8,8,8,8); return t; }
    GradientDrawable bg(int color,float r){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(r);return g;}
    Button btn(String s){Button b=new Button(this);b.setText(s);b.setTextSize(15);b.setAllCaps(false);return b;}
    void showEntry(){
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(12,8,12,12);root.setBackgroundColor(Color.WHITE);setContentView(root);
        LinearLayout head=new LinearLayout(this); head.setGravity(Gravity.CENTER_VERTICAL);
        TextView title=tv("Shree Laxmi Sales",22);title.setTypeface(null,1);head.addView(title,new LinearLayout.LayoutParams(0,60,1));
        Button finalBtn=btn("FINAL TOTAL"); finalBtn.setTextColor(Color.WHITE);finalBtn.setBackground(bg(green,24)); finalBtn.setOnClickListener(v->showFinal());head.addView(finalBtn,new LinearLayout.LayoutParams(135,55));root.addView(head);
        totalView=tv("Total: ₹0",20);totalView.setTypeface(null,1);root.addView(totalView);
        LinearLayout input=new LinearLayout(this);input.setOrientation(LinearLayout.VERTICAL);input.setPadding(0,8,0,8);
        name=new EditText(this);name.setHint("Item name (optional)");name.setSingleLine(true);input.addView(name,new LinearLayout.LayoutParams(-1,55));
        LinearLayout fields=new LinearLayout(this); fields.setGravity(Gravity.CENTER_VERTICAL);
        qty=tv("1",26);qty.setGravity(Gravity.CENTER);qty.setBackground(bg(0xfff1f3f5,18));rate=tv("0",26);rate.setGravity(Gravity.CENTER);rate.setBackground(bg(0xfff1f3f5,18));
        qty.setOnClickListener(v->{active="qty";highlight();});rate.setOnClickListener(v->{active="rate";highlight();});
        fields.addView(qty,new LinearLayout.LayoutParams(0,65,1));TextView x=tv(" × ",25);x.setGravity(Gravity.CENTER);fields.addView(x,new LinearLayout.LayoutParams(42,65));fields.addView(rate,new LinearLayout.LayoutParams(0,65,1));input.addView(fields);root.addView(input);
        itemsBox=new LinearLayout(this);itemsBox.setOrientation(LinearLayout.VERTICAL);root.addView(itemsBox,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout pad=new LinearLayout(this);pad.setOrientation(LinearLayout.VERTICAL);String[][] keys={{"1","2","3"},{"4","5","6"},{"7","8","9"},{"⌫","0","NEXT ITEM"}};
        for(String[] row:keys){LinearLayout r=new LinearLayout(this);for(String k:row){Button q=btn(k);q.setTextSize(k.equals("NEXT ITEM")?13:21);q.setBackground(bg(k.equals("NEXT ITEM")?blue:0xffeeeeee,16));q.setTextColor(k.equals("NEXT ITEM")?Color.WHITE:dark);q.setOnClickListener(v->{ if(k.equals("NEXT ITEM")) addItem(); else if(k.equals("⌫")) backspace(); else key(k);});r.addView(q,new LinearLayout.LayoutParams(0,58,1));}pad.addView(r,new LinearLayout.LayoutParams(-1,62));}root.addView(pad);
        highlight();
    }
    void highlight(){qty.setTextColor(active.equals("qty")?blue:dark);rate.setTextColor(active.equals("rate")?blue:dark);}
    void key(String k){TextView t=active.equals("qty")?qty:rate;String s=t.getText().toString();if(s.equals("0"))s="";if(s.length()<9)t.setText(s+k);}
    void backspace(){TextView t=active.equals("qty")?qty:rate;String s=t.getText().toString();if(s.length()>1)t.setText(s.substring(0,s.length()-1));else t.setText("0");}
    void addItem(){double qv=num(qty),rv=num(rate);if(qv<=0||rv<0){toast("Enter quantity and rate");return;}items.add(new Item(name.getText().toString().trim(),qv,rv));name.setText("");qty.setText("1");rate.setText("0");renderItems();active="rate";highlight();}
    double num(TextView t){try{return Double.parseDouble(t.getText().toString());}catch(Exception e){return 0;}}
    String money(double n){return "₹"+new DecimalFormat("0.##").format(n);}
    void renderItems(){itemsBox.removeAllViews();int i=1;for(Item it:items){TextView t=tv(i+". "+(it.name.isEmpty()?"Item":it.name)+"   "+fmt(it.qty)+" × "+money(it.rate)+" = "+money(it.total()),16);itemsBox.addView(t);i++;}double total=0;for(Item it:items)total+=it.total();totalView.setText("Total: "+money(total));}
    String fmt(double x){return new DecimalFormat("0.##").format(x);}
    void showFinal(){if(items.size()==0){addItem();if(items.size()==0)return;}renderItems();Button gen=btn("GENERATE BILL");gen.setTextColor(Color.WHITE);gen.setBackground(bg(green,24));root.addView(gen,new LinearLayout.LayoutParams(-1,58));gen.setOnClickListener(v->generate());}
    void generate(){if(items.size()==0)return;int inv=nextInvoice();String invoice=month()+" "+String.format(Locale.US,"%02d",inv);String dt=new SimpleDateFormat("dd MMM yyyy (hh:mm a)",Locale.ENGLISH).format(new Date());double total=0;for(Item it:items)total+=it.total();
        Bitmap bm=Bitmap.createBitmap(1080,Math.max(900,260+items.size()*100),Bitmap.Config.ARGB_8888);Canvas c=new Canvas(bm);c.drawColor(Color.WHITE);Paint p=new Paint(1);p.setColor(dark);p.setTypeface(Typeface.create("sans",Typeface.NORMAL));
        p.setTextSize(52);p.setTypeface(Typeface.DEFAULT_BOLD);c.drawText("Shree Laxmi Sales",55,80,p);p.setTypeface(Typeface.DEFAULT);p.setTextSize(30);c.drawText("Mobile: 9811393412",55,125,p);c.drawText("Invoice: "+invoice,55,170,p);c.drawText(dt,55,210,p);c.drawLine(45,235,1035,235,p);int y=285;p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextSize(28);c.drawText("#  Item",55,y,p);c.drawText("Qty",590,y,p);c.drawText("Rate",710,y,p);c.drawText("Amount",865,y,p);y+=55;p.setTypeface(Typeface.DEFAULT);for(int i=0;i<items.size();i++){Item it=items.get(i);c.drawText(""+(i+1)+"  "+(it.name.isEmpty()?"Item":it.name),55,y,p);c.drawText(fmt(it.qty),590,y,p);c.drawText(money(it.rate),710,y,p);c.drawText(money(it.total()),865,y,p);y+=65;}c.drawLine(45,y,1035,y,p);p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextSize(40);c.drawText("TOTAL",650,y+65,p);c.drawText(money(total),865,y+65,p);p.setTextSize(24);p.setTypeface(Typeface.DEFAULT);c.drawText("Thank you!",55,y+65,p);
        try{File dir=new File(getCacheDir(),"images");dir.mkdirs();File f=new File(dir,"bill_"+invoice.replace(" ","_")+".png");FileOutputStream o=new FileOutputStream(f);bm.compress(Bitmap.CompressFormat.PNG,100,o);o.close();Uri uri=FileProvider.getUriForFile(this,"com.shreelaxmisales.app.fileprovider",f);showGenerated(bm,uri,invoice);}catch(Exception e){toast("Could not create bill");}
    }
    void showGenerated(Bitmap bm,Uri uri,String invoice){root.removeAllViews();TextView h=tv("Bill Generated — "+invoice,22);h.setTypeface(null,1);root.addView(h);ImageView iv=new ImageView(this);iv.setImageBitmap(bm);iv.setAdjustViewBounds(true);ScrollView sv=new ScrollView(this);sv.addView(iv);root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));Button sh=btn("SHARE ON WHATSAPP");sh.setTextColor(Color.WHITE);sh.setBackground(bg(green,24));root.addView(sh,new LinearLayout.LayoutParams(-1,62));sh.setOnClickListener(v->{Intent i=new Intent(Intent.ACTION_SEND);i.setType("image/png");i.putExtra(Intent.EXTRA_STREAM,uri);i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);try{i.setPackage("com.whatsapp");startActivity(i);}catch(Exception e){startActivity(Intent.createChooser(i,"Share bill"));}});Button nb=btn("NEW BILL");root.addView(nb,new LinearLayout.LayoutParams(-1,58));nb.setOnClickListener(v->{items.clear();showEntry();});}
    int nextInvoice(){String m=month();String saved=prefs.getString("month","");int n=prefs.getInt("count",0);if(!m.equals(saved))n=0;n++;prefs.edit().putString("month",m).putInt("count",n).apply();return n;}
    String month(){return new SimpleDateFormat("MMM",Locale.ENGLISH).format(new Date()).toUpperCase(Locale.ENGLISH);}
    void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
    static class Item{String name;double qty,rate;Item(String n,double q,double r){name=n;qty=q;rate=r;}double total(){return qty*rate;}}
}
