package cn.edu.scujcc.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;


import cn.edu.scujcc.api.ChannelController;
import cn.edu.scujcc.dao.ChannelRepository;
import cn.edu.scujcc.model.Channel;
import cn.edu.scujcc.model.Comment;

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
	private static final Logger logger = (Logger) LoggerFactory.getLogger(ChannelService.class);
	
	
	/**
	 * 获取所有频道
	 * @param id
	 * @return
	 */
	@Cacheable("channels")
	public List<Channel> getAllChannels() {
		logger.debug("准备从数据库读取所有频道信息");
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
			if(c.getCover() !=null) {
				saved.setCover(c.getCover());
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
	public List<Channel> searchTitle(String title){
		return repo.findByTitle(title);
	}
	public List<Channel> searchQuality(String quality){
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
	
	/**
	 * 想指定的频道添加一条评论
	 * @param channelId 指定的评论编号
	 * @param comment	将要新增的评论对象
	 * @return
	 */
	public Channel addComment(String channelId, Comment comment) {
		Channel saved = getChannel(channelId);
		if(saved != null) {
			saved.addComment(comment);
			return repo.save(saved);
		}
		return null;
	}
	
	/**
	 * 返回指定频道的热门评论
	 * @param channelId 指定频道的编号
	 * @return 热门评论的列表
	 */
	public List<Comment> hotComments(String channelId){
		List<Comment> result = new ArrayList<>();
		Channel saved = getChannel(channelId);
		logger.debug("频道"+channelId+"的数据"+saved);
		if(saved != null && saved.getComments() != null) {
			//根据评论的star进行排序
			saved.getComments().sort(new Comparator<Comment>() {
				@Override
				public int compare(Comment o1, Comment o2) {
					if (o1.getStar() == o2.getStar()) {
						return 0;
					}else if(o1.getStar() < o2.getStar()) {
						return 1;
					}else {
						return -1;
					}					
				}								
			});
			if (saved.getComments().size()>3) {
				result = saved.getComments().subList(0, 3);
			}else {
				result = saved.getComments();
			}
		}
		return result;
	}
}