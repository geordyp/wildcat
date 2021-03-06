package edu.ksu.wildcat;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 *
 * @author geordypaul
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "edu.ksu.wildcat"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private static CodeScanner _codeScanner;

	private static ColorProvider _colorProvider;

	private static JavaTextHover _javaTextHover;

	private static KeywordNode _keywordTree;

	private static ArrayList<String> _mainKeywords;

	private static ArrayList<String> _regularKeywords;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Get the list of keywords
	 *
	 * @return _keywordTree - the tree of keywords
	 */
	private static KeywordNode getKeywordTree() {
		if (_keywordTree == null) {
			try {
				Scanner dictionScanner = new Scanner(new File("C:/Users/geordypaul/Documents/Research/Wildcat/edu.ksu.wildcat/utility/dakota.input.dictionary"));

				_mainKeywords = new ArrayList<String>();
				_regularKeywords = new ArrayList<String>();
				_keywordTree = new KeywordNode("root", null, null, null, null);

				String kywd = dictionScanner.nextLine();
				while (dictionScanner.hasNext()) {
					String desc = "";
					String param = "";
					String line = dictionScanner.nextLine();
					while (!line.contains("KYWD")) {
						if (line.contains("DESC")) {
							desc = line.substring(6);
						}
						else if (line.contains("PARAM")) {
							param = line.substring(7);
						}
						line = dictionScanner.nextLine();
					}
					
					// outside the loop, line holds the next kewyord

					// remove KYWD
					kywd = kywd.substring(5);
					// grab individual keywords
					String[] keywords = kywd.split("/");

					// create keyword node with kywd, desc, and param
					// set parent to root
					KeywordNode parentNode = _keywordTree;
					for (int i = 0; i < keywords.length; i++) {
						String keyword = keywords[i];
						ArrayList<String> aliases = null;

						if (keyword.contains("ALIAS")) {
							aliases = new ArrayList<String>();
							String[] words = keywords[i].split(" ALIAS ");

							// start at 1 because the actual keyword is at 0
							for (int j = 1; j < words.length; j++) {
								aliases.add(words[j]);
								_regularKeywords.add(words[j]);
							}

							// without this keyword could be set to "[word] ALIAS [word]"
							keyword = words[0];
						}

						KeywordNode currNode = parentNode.findChild(keyword);
						if (currNode == null) {
							parentNode.insert(keyword, desc, param, aliases, parentNode);
							if (keywords.length == 1) {
								_mainKeywords.add(keywords[0]);
							}
							else {
								_regularKeywords.add(keyword);
							}
						}
						else {
							parentNode = currNode;
						}
					}

					// set kywd to the next kywd
					kywd = line;
				}

				dictionScanner.close();
			}
			catch (Exception e) {
				System.out.println(e.toString());
			}
		}

		return _keywordTree;
	}

	public static ArrayList<String> getMainKeywords() {
		if (_keywordTree == null)
			_keywordTree = getKeywordTree();
		return _mainKeywords;
	}

	public static ArrayList<String> getRegularKeywords() {
		if (_keywordTree == null)
			_keywordTree = getKeywordTree();
		return _regularKeywords;
	}

	/**
	 * Get this plug-in's code scanner
	 *
	 * @return CodeScanner - RuleBasedScanner
	 */
	public static CodeScanner getMyCodeScanner() {
		if (_codeScanner == null) {
			if (_colorProvider == null)
				_colorProvider = new ColorProvider();

			_codeScanner = new CodeScanner(_colorProvider, getMainKeywords(), getRegularKeywords());
		}
		return _codeScanner;
	}

	/**
	 * Get this plug-in's color provider
	 *
	 * @return ColorProvider - holds the colors for syntax highlighting
	 */
	public static ColorProvider getColorProvider() {
		if (_colorProvider == null)
			_colorProvider = new ColorProvider();
		return _colorProvider;
	}

	/**
	 * Get this plug-in's text hover
	 *
	 * @return ITextHover
	 */
	public static ITextHover getMyTextHover() {
		if (_javaTextHover == null)
			_javaTextHover = new JavaTextHover(getKeywordTree(), getMainKeywords());
		return _javaTextHover;
	}
}
