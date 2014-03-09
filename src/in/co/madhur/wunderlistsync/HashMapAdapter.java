package in.co.madhur.wunderlistsync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import in.co.madhur.wunderlistsync.api.model.WList;

import java.util.ArrayList;
import java.util.List;

public class HashMapAdapter extends BaseAdapter
{
	private final ArrayList<WList> mData;

	public HashMapAdapter(List<WList> list)
	{
		mData = (ArrayList<WList>) list;
	}

	@Override
	public int getCount()
	{
		return mData.size();
	}

	@Override
	public WList getItem(int position)
	{
		return mData.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final View result;

		if (convertView == null)
		{
			result = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
		}
		else
		{
			result = convertView;
		}

		WList item = getItem(position);

		CheckedTextView textView = (CheckedTextView) result.findViewById(android.R.id.text1);

		textView.setText(item.getTitle());

		return result;
	}
}