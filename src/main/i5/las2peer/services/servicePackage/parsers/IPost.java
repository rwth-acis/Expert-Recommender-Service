/**
 * 
 */
package i5.las2peer.services.servicePackage.parsers;

/**
 * @author sathvik
 *
 */
public interface IPost {
    public String getPostId();

    public String getAccAnsId();

    public String getCreationDate();

    public String getScore();

    public String getViewCount();

    public String getBody();

    public String getOwnerUserId();

    public String getLastEditorUserId();

    public String getLastActivityDate();

    public String getTitle();

    public String getTags();

    public String getAnswerCount();

    public String getCommentCount();

    public String getFavoriteCount();

    public String getLastEditDate();

    public String getPostTypeId();

    public String getParentId();
}
