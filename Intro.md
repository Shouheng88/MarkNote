
## NotePal--Android端开源的Markdown笔记应用

### 简介

NotePal是一款开源的Markdown笔记应用，可以供初级或中级安卓开发者进行学习。该笔记目前的功能比较完善，如果你有做一个属于自己的Markdown笔记的想法，那么你可以参考一些它的源代码。当然，该软件开源的目的是希望借助社区的力量丰富软件的功能，如果你有改善它的想法也可以在Github中向该项目提交代码。

在这里，我从该软件的基本功能和这些功能的实现方式两个方面介绍一下这款应用。不过首先，我们还是来看一下该软件的一些截图：

### 部分截图

![Screenshots](https://github.com/Shouheng88/NotePal/blob/master/screenshots/note_view_page.png)

![Screenshots](https://github.com/Shouheng88/NotePal/blob/master/screenshots/note_view_page_2.png)

如图，该软件采用了Materiald Design的设计风格，其中涉及到了一些基本的支持包里的控件，当然也引用了Github上面一些开源的库。如果你是一个初学者，并且对Material Design感兴趣的话，不妨参考一下它的代码。

### 功能特性

* 基本的**增加**, **归档**, **放进垃圾箱**和**彻底删除** 操作
* Markdown语法支持，包括: **标题, 数字列表, 多选框, 上下脚标, 加粗, 倾斜, 数学公式, 表格, 选中, 图片和链接等**
* **时间线**用来记录在程序中的操作，特别是一些数据库的操作
* **文件, 视频, 音频, 图片, 手写, 位置**以及其他的多媒体支持
* 多彩的**图表**用于统计用户信息
* 多主题支持，包括：**日夜间主题, 13种主题色, 16种强调色以及是否对导航栏着色**
* **桌面小控件**包括列表和工具栏，以及**桌面快捷方式**
* 多彩的**标签**，可以选择图标和颜色
* 两种笔记管理方式，包括标签和**层级结构**
* 多种笔记导出方式，包括：**PDF, txt, md以及html**
* **应用独立锁**
* **备份到外部存储设备**以及**备份到OneDrive**
* **图片压缩**

### 功能实现

#### 1.数据库设计

根据上面的介绍，本程序中使用了标签和层级结构两种管理方式，顺便还有时间线的功能，所以我们先介绍一下数据库表的设计。

该软件中所有的数据库对象均继承自Model类，它其中定义了基本的列信息，注意在程序中我们用了两个自定义的注解Column和Table，分别用来指定数据库的列名和表名。

```

	public class Model implements Serializable {

		@Column(name = "id")
		protected long id;

		@Column(name = "code")
		protected long code;

		@Column(name = "user_id")
		protected long userId;

		// ....
	}
	
	@Table(name = "gt_note")
	public class Note extends Model implements Parcelable {

		@Column(name = "parent_code")
		private long parentCode;
		
		// ....		
	}

```

如上所示，我们在Model中用Column指定了所有的数据库对象都需要的字段。然后，对笔记类型Note，让它继承Model，并且用Table指定了它的表名。最后，在Note中定义了一些笔记对象的数据库信息。

说完了数据库对象，我们看一下数据库查询对象。因为在我们的程序中需要对数据库查询等的方法做一些额外的操作，所以没有直接使用开源的数据库的库，比如Room等，而是自己设计了一套。在我们的数据库操作中也有一些便利的地方，比如我们可以只进行简单的配置就能完成一个新的对象的数据库查询操作，而不用为其添加任何SQL。这种数据库的设计用到的都是原生Android数据库的知识，所以如果基础扎实的话并不难理解。

public class PalmDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "NotePal.db";
    private static final int VERSION = 7;

    private Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static PalmDB sInstance = null;

    private volatile boolean isOpen = true;

    public static PalmDB getInstance(final Context context){
        if (sInstance == null){
            synchronized (PalmDB.class) {
                if (sInstance == null) {
                    sInstance = new PalmDB(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private PalmDB(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        NotesStore.getInstance(mContext).onCreate(db);
        // ... 
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        NotesStore.getInstance(mContext).onUpgrade(db, oldVersion, newVersion);
		// ...
    }
}

如上所示，我们用PalmDB继承了SQLiteOpenHelper，并在它的onUpgrade和onCreate调用各个查询对象的更新和创建方法。而且这是一个单例的对象，因为一个数据库只要有一个SQLiteOpenHelper就够了，所以我们只要一个单例的就够了。


