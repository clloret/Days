package com.clloret.days.menu.items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.clloret.days.R;
import com.clloret.days.domain.events.filter.EventFilterByTag;
import com.clloret.days.domain.tags.order.TagSortable;
import com.clloret.days.menu.items.holders.TextViewHolder;
import com.clloret.days.model.entities.TagViewModel;

public class DrawerTag extends DrawerFilter implements TagSortable {

  private final DrawerTagSelectedMgr drawerTagSelectedMgr;
  private TagViewModel tag;

  public DrawerTag(@NonNull EventFilterByTag eventFilterByTag,
      @NonNull TagViewModel tag, @NonNull DrawerTagSelectedMgr drawerTagSelectedMgr) {

    super(tag.getName(), R.drawable.ic_label_24dp, eventFilterByTag);

    this.tag = tag;
    this.drawerTagSelectedMgr = drawerTagSelectedMgr;
  }

  @Override
  public View getView(LayoutInflater inflater, ViewGroup parent) {

    View convertView = inflater.inflate(R.layout.menu_adapter_texticon, parent, false);

    setView(convertView);

    return convertView;
  }

  @Override
  public void select(FragmentActivity activity) {

    drawerTagSelectedMgr.select(this);

    super.select(activity);
  }

  @Override
  public int getType() {

    return 3;
  }

  @Override
  public TextViewHolder createViewHolder() {

    return new TextViewHolder();
  }

  @Override
  public void deselect() {

    drawerTagSelectedMgr.deselect();
  }

  @Override
  public String getName() {

    return tag.getName();
  }

  @NonNull
  public TagViewModel getTag() {

    return tag;
  }

  public void setTag(TagViewModel tag) {

    this.tag = tag;

    updateTitle();
  }

  private void updateTitle() {

    title = tag.getName();
  }
}
