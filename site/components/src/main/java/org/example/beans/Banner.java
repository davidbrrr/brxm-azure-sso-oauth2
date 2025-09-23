package org.example.beans;
/*
 * Copyright 2014-2025 Bloomreach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;
import org.hippoecm.hst.content.beans.standard.HippoBean;

@HippoEssentialsGenerated(internalName = "myproject15azuressooauth2:bannerdocument")
@Node(jcrType = "myproject15azuressooauth2:bannerdocument")
public class Banner extends BaseDocument {
	@HippoEssentialsGenerated(internalName = "myproject15azuressooauth2:title")
	public String getTitle() {
		return getSingleProperty("myproject15azuressooauth2:title");
	}

	@HippoEssentialsGenerated(internalName = "myproject15azuressooauth2:content")
	public HippoHtml getContent() {
		return getHippoHtml("myproject15azuressooauth2:content");
	}

	@HippoEssentialsGenerated(internalName = "myproject15azuressooauth2:image")
	public HippoGalleryImageSet getImage() {
		return getLinkedBean("myproject15azuressooauth2:image", HippoGalleryImageSet.class);
	}

	@HippoEssentialsGenerated(internalName = "myproject15azuressooauth2:link")
	public HippoBean getLink() {
		return getLinkedBean("myproject15azuressooauth2:link", HippoBean.class);
	}
}
