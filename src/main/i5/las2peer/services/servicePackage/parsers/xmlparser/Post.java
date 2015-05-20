package i5.las2peer.services.servicePackage.parsers.xmlparser;

import i5.las2peer.services.servicePackage.parsers.IPost;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML binding to the posts.xml.
 * This contains the properties of a single post made by the user.
 * JAXB is used to parse the tags of an XML.
 * 
 * @author sathvik
 */

@XmlRootElement(name = "row")
public class Post implements IPost {

    @XmlAttribute(name = "Id")
    String postid;

    @XmlAttribute(name = "PostTypeId")
    String post_type_id;

    @XmlAttribute(name = "AcceptedAnswerId")
    String accept_ans_id;

    @XmlAttribute(name = "CreationDate")
    String creation_date;

    @XmlAttribute(name = "Score")
    String score;

    @XmlAttribute(name = "ViewCount")
    String view_count;

    @XmlAttribute(name = "Body")
    String body;

    @XmlAttribute(name = "OwnerUserId")
    String owner_user_id;

    @XmlAttribute(name = "LastEditorUserId")
    String last_editor_user_id;

    @XmlAttribute(name = "LastEditDate")
    String last_edit_date;

    @XmlAttribute(name = "LastActivityDate")
    String last_activity_date;

    @XmlAttribute(name = "Title")
    String title;

    @XmlAttribute(name = "Tags")
    String tags;

    @XmlAttribute(name = "AnswerCount")
    String answer_count;

    @XmlAttribute(name = "CommentCount")
    String comment_count;

    @XmlAttribute(name = "FavoriteCount")
    String favorite_count;

    @XmlAttribute(name = "ParentId")
    String parent_id;

    public Post() {

    }

    public String getPostId() {
	return this.postid;
    }

    public String getAccAnsId() {
	return this.accept_ans_id;
    }

    public String getCreationDate() {
	return this.creation_date;
    }

    public String getScore() {
	return this.score;
    }

    public String getViewCount() {
	return this.view_count;
    }

    public String getBody() {
	return this.body;
    }

    public String getOwnerUserId() {
	return this.owner_user_id;
    }

    public String getLastEditorUserId() {
	return this.last_editor_user_id;
    }

    public String getLastActivityDate() {
	return this.last_activity_date;
    }

    public String getTitle() {
	return this.title;
    }

    public String getTags() {
	return this.tags;
    }

    public String getAnswerCount() {
	return this.answer_count;
    }

    public String getCommentCount() {
	return this.comment_count;
    }

    public String getFavoriteCount() {
	return this.favorite_count;
    }

    public String getLastEditDate() {
	return this.last_edit_date;
    }

    public String getPostTypeId() {
	return this.post_type_id;
    }

    public String getParentId() {
	return this.parent_id;
    }

}
