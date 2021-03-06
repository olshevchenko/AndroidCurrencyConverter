/**
 * Created by oshevchenk on 25.06.2015.
 */

package com.example.ol.currconverter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


public class OperationsFragment extends Fragment {
  //for logging
  private static final String LOG_TAG = OperationsFragment.class.getName();

  //for interaction w. Activity & Session
  private MainActivity myActivity;
  private Session mySession = null;

  private OpListAdapter opListAdapter = null;

  private ListView lvUserOpsHistory = null;
  private int lvUOHItemPosition = -1;
  private boolean lvUOHIsOnTouchListenerSet = false;

  //for swipe detection
  private int REL_SWIPE_MIN_DISTANCE;
  private int REL_SWIPE_MIN_VELOCITY;
  private int REL_SWIPE_MAX_OFFSET;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.i(LOG_TAG, getTag() + " onCreate()");

    DisplayMetrics dm = getResources().getDisplayMetrics();
    REL_SWIPE_MIN_DISTANCE = (int) (120.0f * dm.densityDpi / 160.0f + 0.5);
    REL_SWIPE_MIN_VELOCITY = (int) (200.0f * dm.densityDpi / 160.0f + 0.5);
    REL_SWIPE_MAX_OFFSET = (int) (250.0f * dm.densityDpi / 160.0f + 0.5);

    myActivity = (MainActivity) getActivity();
    mySession = myActivity.getUserSession();

//    Toast.makeText(getActivity(), LOG_TAG + "onCreate()", Toast.LENGTH_SHORT).show();
    Log.i(LOG_TAG, "onCreate(): myActivity: " + myActivity + ", mySession: " + mySession);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    Log.i(LOG_TAG, getTag() + "onCreateView()");
    View rootView = inflater.inflate(R.layout.fragment_operations, container, false);

    lvUserOpsHistory = (ListView) rootView.findViewById(R.id.lvUserOpsHistory);
   
    Button btClearAll = (Button) rootView.findViewById(R.id.btClearAll);
    btClearAll.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        Log.d(LOG_TAG, "Clearing ALL user's ('" + mySession.getUserName() + "') operation records...");
        mySession.getOpHelper().onClear();
        opListAdapter.notifyDataSetChanged();
      }
    });

    return rootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    Log.i(LOG_TAG, getTag() + " onActivityCreated()");
  }

  @Override
  public void onResume() {
    super.onResume();

    Log.i(LOG_TAG, getTag() + " onResume()");
    getRetainInstance();
/*
    Log.i(LOG_TAG, "onResume(): myActivity: " + myActivity + "mySession: " + mySession);
    Log.i(LOG_TAG, "onResume(): lvUserOpsHistory: " + lvUserOpsHistory);
    Log.i(LOG_TAG, "onResume(): opListAdapter: " + opListAdapter);
    Log.i(LOG_TAG, "onResume(): OnItemLongClickListener(): " + lvUserOpsHistory.getOnItemLongClickListener());
*/
    // (re)set currencies list adapter (re)created
    if (null == lvUserOpsHistory.getAdapter()) {
      if (null == opListAdapter) {
        opListAdapter = new OpListAdapter(myActivity, mySession.getOpHelper().getUserOperationsHistory());
      }
      lvUserOpsHistory.setAdapter(opListAdapter);
    }

    //restore list position
    if (lvUOHItemPosition == -1)
      lvUserOpsHistory.setSelection(0);
    else
      lvUserOpsHistory.setSelection(lvUOHItemPosition);

    // (re)set OnItemLongClickListener
    if (null != lvUserOpsHistory.getOnItemLongClickListener())
      ;
    else {
      lvUserOpsHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {
          lvUOHItemPosition = position;
          Toast toast = Toast.makeText(myActivity, R.string.toastSwipeHoriz, Toast.LENGTH_SHORT);
          toast.setGravity(Gravity.CENTER, 0, 0);
          toast.show();
          return true;
        }
      });
    } //else

    ///for swipe animation
    MyAnimationListener myAnimationListener = new MyAnimationListener();

    ///for swipe detection
    @SuppressWarnings("deprecation")
    final GestureDetector gestureDetector =
        new GestureDetector(new MyGestureListener(lvUserOpsHistory, opListAdapter, myAnimationListener));

    // (re)set OnItemLongClickListener
    if (lvUOHIsOnTouchListenerSet)
      ;
    else {
      lvUserOpsHistory.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
          if (0 == opListAdapter.getCount())
            return true; ///ignore events for empty list
          else
            return gestureDetector.onTouchEvent(event);
        }
      });
      lvUOHIsOnTouchListenerSet = true;
    }

    Log.d(LOG_TAG, getTag() + ": opListAdapter = " + opListAdapter);
    Log.d(LOG_TAG, getTag() + ": lvUserOpsHistory = " + lvUserOpsHistory);
  }

  @Override
  public void onPause() {
    super.onPause();
    Log.i(LOG_TAG, getTag() + " onPause()");
    setRetainInstance(true);
  }

  public void getHorizSwipeItem(int position) {
    if ((null == mySession) || (null == opListAdapter) ) {
      Log.w(LOG_TAG, "Undefined UserSession || OpListAdapter - ignoring 'swipe' event...");
      return;
    }
    mySession.getOpHelper().onDelete(position, false); ///use delayed deletion yet..
    opListAdapter.notifyDataSetChanged();
    return;
  }

  class MyAnimationListener implements Animation.AnimationListener {
    private View mListItem;
    private int mPosition;

    public void setListItem(View listItem) {
      this.mListItem = listItem;
    }

    public void setPosition(int position) {
      this.mPosition = position;
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
      mListItem.setEnabled(true);
      getHorizSwipeItem(mPosition);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
  }

  class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
    private int temp_position = -1;
    private ListView mList;
    private OpListAdapter mOpListAdapter;
    private MyAnimationListener mAnimationListener;
    private View mListItem;

    MyGestureListener(ListView list, OpListAdapter opListAdapter, MyAnimationListener animationListener) {
      mList = list;
      mOpListAdapter = opListAdapter;
      mAnimationListener = animationListener;
    }

    @Override
    public boolean onDown(MotionEvent e) {
      temp_position = mList.pointToPosition((int) e.getX(), (int) e.getY());
      lvUOHItemPosition = temp_position;
      return super.onDown(e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

//      Toast.makeText(myActivity, "Starting onFling()..", Toast.LENGTH_SHORT).show();

      float distanceX = e2.getX() - e1.getX();
      float distanceY = e2.getY() - e1.getY();

      if (Math.abs(distanceY) > REL_SWIPE_MAX_OFFSET)
        return false;

      //get list item number
      final int pos = mList.pointToPosition((int) e1.getX(), (int) e2.getY());

      if (pos >= 0 && temp_position == pos)
        ; //it's ok with item position search
      else
        return false;

      if (Math.abs(distanceX) > REL_SWIPE_MIN_DISTANCE &&
          Math.abs(velocityX) > REL_SWIPE_MIN_VELOCITY) {
        mListItem = mOpListAdapter.getViewByPosition(pos, mList);
//        mList.setEnabled(false); /// prevent unwanted additional list events

        ///prepare parameters data for animation
        mAnimationListener.setListItem(mListItem);
        mAnimationListener.setPosition(pos);

        if (distanceX > 0) {
          Animation anim = AnimationUtils.loadAnimation(myActivity, R.anim.operations_item_trans_to_right);
          anim.setAnimationListener(mAnimationListener);
          mListItem.startAnimation(anim);
        }
        else {
          Animation anim = AnimationUtils.loadAnimation(myActivity, R.anim.operations_item_trans_to_left);
          anim.setAnimationListener(mAnimationListener);
          mListItem.startAnimation(anim);
        }
      }

      return false;
    } //onFling()

  } //class MyGestureListener

} //class OperationsFragment
