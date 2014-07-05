# Jabref-plugins

This is a small collection of plugins for the BibTex editor [JabRef](http://jabref.sourceforge.net/).
Currently it contains plugins:

* fetchArxiv
* renamefile

In order to install any plugin one has to

1. download the jar file for the plugin
2. go to Plugins/Manage plugins/Install plugin in JabRef
3. install the downloaded file


### fetchArxiv
This plugin can be used to download a preprint information from arxiv.org by
providing its arXiv ID. The ID is saved in the BibTex entry field "eprint".

### renamefile
This plugin can be used to rename/copy/delete files attached to BibTex entries.
The file information is stored in the entry field "file". Files can be renamed
according to their BibTeX entry. The rename pattern uses an extended syntax of
the [BibTeX key generator](http://jabref.sourceforge.net/help/LabelPatterns.php) of JabRef.
For more infromation and examples see the help page of the plugin.

The newest versions can be found [here](https://github.com/korv/Jabref-plugins/releases).
The older versions can be found [here](https://github.com/korv/Jabref-plugins/downloads).



