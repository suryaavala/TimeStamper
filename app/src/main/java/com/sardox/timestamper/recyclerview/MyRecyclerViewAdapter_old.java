package com.sardox.timestamper.recyclerview;


public class MyRecyclerViewAdapter_old {//extends RecyclerView.Adapter<MyRecyclerViewAdapter_old.MyViewHolder> {
/*

    private List<Timestamp> mStampsList;

    private FragmentManager fragmentmanager;


    public MyRecyclerViewAdapter_old(List<Timestamp> stampsList) {
        this.mStampsList = stampsList;
    }

    @Override
    public MyRecyclerViewAdapter_old.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recyclerview_item, viewGroup,
                        false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MyRecyclerViewAdapter_old.MyViewHolder holder, int position) {
        Timestamp timestamp = mStampsList.get(position);

        Calendar c = Calendar.getInstance();
        TimeZone tz = c.getTimeZone();

        c.setTimeInMillis(timestamp.getTime());


        String format;

        if (mainActivity.use24hrFormat) {
            if (mainActivity.showMillis) {
                format = "HH:mm:ss.SSS";

            } else format = "HH:mm:ss";
        } else {
            if (mainActivity.showMillis) {
                format = "hh:mm:ss.SSS a";

            } else format = "hh:mm:ss a";

        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(tz);
        String hhmmss = sdf.format(c.getTime());
        holder.mNameTextView.setText(hhmmss);

        SimpleDateFormat sdfDAY = new SimpleDateFormat("d MMM");
        sdfDAY.setTimeZone(tz);
        String dMMM = sdfDAY.format(c.getTime());
        holder.mPosTextView.setText(dMMM);

        SimpleDateFormat sdfWeekDay = new SimpleDateFormat("EEE");
        sdfWeekDay.setTimeZone(tz);
        String EEE = sdfWeekDay.format(c.getTime());
        holder.mWeekDay.setText(EEE);

        holder.mGPSpinButton.setVisibility(View.INVISIBLE);
        String subtitle = timestamp.getSubtitle();

        Log.e("gps", "holder for: " + position + ", its gps: " + timestamp.getGps());
        if (timestamp.getGps() != "") {
            holder.mGPSpinButton.setVisibility(View.VISIBLE);
        }
        holder.mSubtitleTextView.setText(subtitle);

        holder.mCategoryTextView.setText(mainActivity.getCategoryByID(timestamp.getCategoryID()).getName());

    }

    @Override
    public int getItemCount() {
        return mStampsList.size();
    }

    public void sortByCategory(int categoryID) {
        mStampsList.clear();

        Collections.sort(mStampsListCopy, new Comparator() {

            public int compare(Object arg0, Object arg1) {
                if (!(arg0 instanceof Timestamp)) {
                    return -1;
                }
                if (!(arg1 instanceof Timestamp)) {
                    return -1;
                }
                Timestamp pers0 = (Timestamp) arg0;
                Timestamp pers1 = (Timestamp) arg1;
                return (int) (pers0.getTime() - pers1.getTime());
            }
        });


        if (categoryID == 0) {
            mStampsList.addAll(mStampsListCopy);
            notifyDataSetChanged();
            //Log.e("aaa", "category sorted=0");
            return;
        }

        for (final Timestamp item : mStampsListCopy) {

            if (item.getCategoryID() == categoryID) {
                mStampsList.add(item);
            }
        }
        //  Log.e("aaa", "mStampsList.size after sorting: " + mStampsList.size());


        notifyDataSetChanged();
    }


    public int findStampPositionInListByID(int stampID) {
        int a = 0;
        for (final Timestamp item : mStampsListCopy) {
            if (item.getStampID() == stampID) {
                return a;
            }
            a++;
        }
        return -1;
    }


    public void addStamp(Timestamp timestamp) {

        mStampsListCopy.add(timestamp);
        mStampsList.add(timestamp);


        notifyDataSetChanged();
        sortByCategory(mainActivity.LastCategoryFilter);
        mainActivity.recyclerView.scrollToPosition(mStampsList.size() - 1);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView mNameTextView;
        private TextView mSubtitleTextView;
        private TextView mPosTextView;
        private TextView mCategoryTextView;
        private ImageButton mGPSpinButton;
        private TextView mWeekDay;

        public MyViewHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.textViewTime);
            mSubtitleTextView = (TextView) itemView.findViewById(R.id.textViewNote);
            mPosTextView = (TextView) itemView.findViewById(R.id.textViewPos);
            mCategoryTextView = (TextView) itemView.findViewById(R.id.textViewCategory);
            mGPSpinButton = (ImageButton) itemView.findViewById(R.id.gpsPinButton);
            mWeekDay = (TextView) itemView.findViewById(R.id.textViewWeekday);

            mNameTextView.setOnClickListener(this);
            mNameTextView.setOnLongClickListener(this);
            mCategoryTextView.setOnClickListener(this);
            mGPSpinButton.setOnClickListener(this);
            mSubtitleTextView.setOnClickListener(this);

        }

        @Override
        public boolean onLongClick(View v) {
            if (v.getId() == mNameTextView.getId()) {

                DatePickerFragment.time = mStampsList.get(getAdapterPosition()).getTime();
                TimePickerFragment.pos = getAdapterPosition();
                DatePickerFragment.fragmentmanager = fragmentmanager;
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(fragmentmanager, "Change the date");
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            int a = getAdapterPosition();
            if (v.getId() == mNameTextView.getId() || v.getId() == mSubtitleTextView.getId()) {

                showDialog(v, getAdapterPosition()); // input dialog -- for a note

            } else if (v.getId() == mGPSpinButton.getId()) { //delete item from list

                String gps = mStampsList.get(a).getGps();
                Log.e("aaa", "gps: " + mStampsList.get(a).getGps());


                String uri = "geo:0,0?q=" + gps + "(" + mStampsList.get(a).getSubtitle() + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                try {
                    v.getContext().startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(v.getContext(), "Please install a maps application", Toast.LENGTH_LONG).show();
                }


            } else if (v.getId() == mCategoryTextView.getId()) {

                showSpinner(v, getAdapterPosition()); // spinner dialog -- change items category

            } else if (v.getId() == mSubtitleTextView.getId()) {

                showDialog(v, getAdapterPosition()); // input dialog -- for a note

            }
        }


        private void showSpinner(View v, final int pos) {
            AlertDialog.Builder b = new AlertDialog.Builder(v.getContext());
            b.setTitle("Select a category");

            List<String> types = new ArrayList<>();// = {"Sport", "Work shifts", "Gym training"};

            for (Category item : mainActivity.getCategoryList()) {   //for (Category item : mainActivity.CategoryList
                types.add(item.getName());
            }

            b.setItems(types.toArray(new String[types.size()]), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int clickedPos) {
                    dialog.dismiss();

                    int newCategoryID = mainActivity.getCategoryList().get(clickedPos).getCategoryID(); //int newCategoryID = mainActivity.CategoryList.get(clickedPos).getCategoryID();

                    int ourItemIDInList = mStampsList.get(pos).getStampID();
                    mStampsListCopy.get(findStampPositionInListByID(ourItemIDInList)).setCategoryID(newCategoryID);

                    // mStampsListCopy.get(findStampPositionInListByID(mStampsList.get(pos).getStampID())).setCategoryID(clickedPos);

                    // mStampsList.get(pos).setCategoryID(clickedPos);
                    notifyDataSetChanged();
                    mStampsList.remove(pos);
                    mainActivity.filterList();

                }

            });

            b.show();

        } // spinner dialog -- change items category


        public void showDialog(View v, final int pos) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            View viewInflated = LayoutInflater.from(v.getContext()).inflate(R.layout.text_input_note, null, false);
            final EditText input = (EditText) viewInflated.findViewById(R.id.input);
            builder.setView(viewInflated);

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    mStampsListCopy.get(findStampPositionInListByID(mStampsList.get(pos).getStampID())).setSubtitle(input.getText().toString());

                    mStampsList.get(pos).setSubtitle(input.getText().toString());

                    notifyDataSetChanged();


                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });


            builder.show().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }  // input dialog -- for a note
    }
    */
}