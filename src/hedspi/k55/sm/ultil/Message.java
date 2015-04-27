package hedspi.k55.sm.ultil;

import java.net.MalformedURLException;
import java.net.URL;

import android.text.Html;

public class Message implements Comparable<Message> {

	private String title;
	private URL link;
	private String source;
	private String description;

	public String getTitle() {
		return Html.fromHtml(title).toString();
	}

	public void setTitle(String title) {
		this.title = title.trim();
	}

	// getters and setters omitted for brevity
	public String getLink() {
		return link.toExternalForm();
	}

	public String getDescription() {
		return description;
	}

	public String getSource() {
		return source;
	}

	public void setDescription(String description) {
		this.description = description.trim();
		String htmlformat = Html.fromHtml(description).toString();
		StringBuffer desc = new StringBuffer(htmlformat);
		desc.delete(0, desc.indexOf("\n"));
		desc.deleteCharAt(0);
		desc.deleteCharAt(desc.indexOf("\n") + 1);
		int endOfDesc = desc.indexOf("\n", desc.indexOf("\n") + 1);
		desc.delete(endOfDesc, desc.length());
		this.source = desc.substring(0, desc.indexOf("\n"));
		desc.delete(0, desc.indexOf("\n") + 1);
		this.description = desc.toString();
	}

	public void setLink(String link) {
		try {
			this.link = new URL(link);

		} catch (MalformedURLException e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
	}

	public Message copy() {
		Message copy = new Message();
		copy.title = title;
		copy.link = link;
		copy.description = description;
		return copy;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Title: ");
		sb.append(title);
		sb.append('\n');
		sb.append("Link: ");
		sb.append(link);
		sb.append('\n');
		sb.append("Description: ");
		sb.append(description);
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public int compareTo(Message another) {
		// TODO Auto-generated method stub
		if (another == null)
			return 1;
		else
			return another.title.compareTo(title);
	}
}
