package cn.edu.scujcc.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.classic.Logger;
import cn.edu.scujcc.dao.ChannelRepository;
import cn.edu.scujcc.model.Channel;

/**
 * 
 *提供频道相关逻辑业务
 * @author 50375
 *
 */
@Service
public class ChannelService {
	@Autowired
	private ChannelRepository repo;
	
	
	/**
	 * 获取所有频道
	 * @param id
	 * @return
	 */
	public List<Channel> getAllChannels() {
		return repo.findAll();
	}
	/**
	 * 获取一个频道
	 * @param id
	 * @return
	 */
	public Channel getChannel(String channelId) {
	Optional<Channel> result = repo.findById(channelId);
	
	if(result.isPresent()) {
		return result.get();
	}else {
		return null;
	}
}

	/**
	 * 删除一个 频道
	 * @param id
	 * @return
	 */
	public boolean deleteChannel(String channelId) {
		boolean result = true;
		repo.deleteById(channelId);
		return result;
	}
	
	/**
	 * 新建一个频道
	 * @param c
	 * @return
	 */
	public Channel createChannel(Channel c) {
		return repo.save(c);
	}
	
	/**
	 * 更新一个频道
	 * @param 待更新的频道
	 * @return 更新后的频道
	 */
	public Channel updateChannel(Channel c) {
		Channel saved = getChannel(c.getId());
		if (saved != null) {
			if(c.getTitle() !=null) {
				saved.setTitle(c.getTitle());
			}
			if(c.getQuality() !=null) {
				saved.setQuality(c.getQuality());
			}
			if(c.getUrl() !=null) {
				saved.setUrl(c.getUrl());
			}
			if(c.getComments() !=null) {
				saved.getComments().addAll(c.getComments());
			}else {
				saved.setComments(c.getComments());
			}
		}
		return repo.save(saved);
	}
	
	
	/**搜索方法
	 * 
	 */
	//public List<Channel> search(String title, String quality){
	//	return repo.findByTitleAndQuality(title, quality);
	//}
	public List<Channel> chazhaotitle(String title){
		return repo.findByTitle(title);
	}
	public List<Channel> chazhaoquality(String quality){
		return repo.findByQuality(quality);
	}
	
	/**
	 * 找出今天有评论的频道
	 * 
	 */
	public List<Channel> getLatesCommentsChannel(){
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime today = LocalDateTime.of(now.getYear(),now.getMonthValue(),now.getDayOfMonth(),0,0);
		return repo.findByCommentsDtAfter(today);
	}

}