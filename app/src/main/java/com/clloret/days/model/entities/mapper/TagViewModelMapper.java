package com.clloret.days.model.entities.mapper;

import com.clloret.days.domain.entities.Tag;
import com.clloret.days.model.entities.TagViewModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TagViewModelMapper {

  @Inject
  public TagViewModelMapper() {

  }

  public Tag toTag(TagViewModel viewModel) {

    Tag tag = null;
    if (viewModel != null) {
      tag = new Tag(viewModel.getId(), viewModel.getName());
    }
    return tag;
  }

  public TagViewModel fromTag(Tag tag) {

    TagViewModel viewModel = null;
    if (tag != null) {
      viewModel = new TagViewModel(tag.getId(), tag.getName());
    }
    return viewModel;
  }

  public List<TagViewModel> fromTag(Collection<Tag> tagCollection) {

    final List<TagViewModel> viewModels = new ArrayList<>(20);
    for (Tag tag : tagCollection) {
      final TagViewModel viewModel = fromTag(tag);
      if (viewModel != null) {
        viewModels.add(viewModel);
      }
    }
    return viewModels;
  }

}
