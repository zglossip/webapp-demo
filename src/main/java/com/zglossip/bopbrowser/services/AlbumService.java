package com.zglossip.bopbrowser.services;

import com.zglossip.bopbrowser.clients.BasicClient;
import com.zglossip.bopbrowser.clients.DeezerAlbumClient;
import com.zglossip.bopbrowser.clients.DeezerSearchClient;
import com.zglossip.bopbrowser.domains.Album;
import com.zglossip.bopbrowser.domains.AlbumStub;
import com.zglossip.bopbrowser.domains.adaptor.deezer.AlbumDeezerAdaptor;
import com.zglossip.bopbrowser.domains.adaptor.deezer.AlbumStubDeezerAdaptor;
import com.zglossip.bopbrowser.domains.adaptor.deezer.SongStubDeezerAdaptor;
import com.zglossip.bopbrowser.domains.models.deezer.DeezerSongList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AlbumService extends AbstractService<AlbumStub> {

  private final DeezerAlbumClient deezerAlbumClient;
  private final DeezerSearchClient deezerSearchClient;
  private final BasicClient basicClient;
  private final GenreService genreService;

  @Autowired
  public AlbumService(final DeezerAlbumClient deezerAlbumClient, final DeezerSearchClient deezerSearchClient,
                      final BasicClient basicClient, final GenreService genreService) {
    this.deezerAlbumClient = deezerAlbumClient;
    this.deezerSearchClient = deezerSearchClient;
    this.basicClient = basicClient;
    this.genreService = genreService;
  }

  public Album getAlbumInfo(final int id) {
    final AlbumDeezerAdaptor album = deezerAlbumClient.getAlbumInfo(id);

    if (Objects.isNull(album)) {
      return null;
    }

    final DeezerSongList songList = basicClient.getRequest(album.getTracklist(), DeezerSongList.class);

    if (Objects.nonNull(songList) && Objects.nonNull(songList.getData())) {
      album.setSongList(songList.getData().stream().map(SongStubDeezerAdaptor::clone).collect(Collectors.toList()));
    }

    return album;
  }

  public AlbumStub getAlbumStub(final int id) {
    return deezerAlbumClient.getAlbumStub(id);
  }

  @Override
  public List<? extends AlbumStub> search(final String query) {
    final List<AlbumStubDeezerAdaptor> searchResults = deezerSearchClient.searchAlbums(query);
    genreService.populateAlbumStubGenre(searchResults);
    return searchResults;
  }
}
