package configurationslicing.logstash;

        import java.util.Collections;
        import java.util.List;

        import jenkins.plugins.logstash.LogstashNotifier;

        import hudson.Extension;
        import hudson.Util;
        import hudson.maven.AbstractMavenProject;
        import hudson.maven.MavenModule;
        import hudson.maven.MavenModuleSet;
        import hudson.model.AbstractProject;
        import hudson.model.Descriptor;
        import hudson.model.Project;
        import hudson.tasks.BuildWrapper;
        import hudson.tasks.Publisher;
        import hudson.util.DescribableList;
        import configurationslicing.BooleanSlicer;
        import configurationslicing.TopLevelItemSelector;

/**
 * Created by zhouxiaoqing on 2017/3/2.
 */

@Extension
public class LogStashPublisherSlicer extends BooleanSlicer<AbstractProject<?,?>> {
    public LogStashPublisherSlicer() {
        super(new LogstashSpec());
    }
    public static class LogstashSpec implements BooleanSlicer.BooleanSlicerSpec<AbstractProject<?,?>> {

        @Override
        public String getName() {
            return "Logstash Publisher Slicer";
        }

        @Override
        public String getUrl() {
            return "logstash.publisher";
        }

        public List<AbstractProject<?,?>> getWorkDomain() {
            return TopLevelItemSelector.getAllTopLevelItems(AbstractProject.class);
        }

        public boolean getValue(AbstractProject<?,?> item) {
            DescribableList<Publisher, Descriptor<Publisher>> publishersList =
                    getPublishers(item);
            if(publishersList == null) {
                return false;
            }
            return !publishersList.getAll(LogstashNotifier.class).isEmpty();
        }

        private DescribableList<Publisher, Descriptor<Publisher>> getPublishers(
                AbstractProject<?, ?> item) {
            if(item instanceof Project) {
                return ((Project)item).getPublishersList();
            } else if(item instanceof MavenModuleSet) {
                return ((MavenModuleSet)item).getPublishersList();
            } else {
                return null;
            }
        }

        @Override
        public String getName(AbstractProject<?, ?> item) {
            return item.getName();
        }

        @Override
        public boolean setValue(AbstractProject<?, ?> item, boolean value) {
            LogstashNotifier logstashnotifier = new LogstashNotifier(-1, false);
            if(item instanceof Project) {
                DescribableList pList = ((Project)item).getPublishersList();
                List<LogstashNotifier> lsList =
                        Util.filter(pList, LogstashNotifier.class);
                if(lsList.isEmpty() != value) {
                    // already matches value.  Do nothing.
                    return false;
                }
                if(value) {
                    pList.add(new LogstashNotifier(-1, false));
                } else {
                    pList.removeAll(lsList);
                }
                return true;
            } else if(item instanceof MavenModuleSet) {
                DescribableList pList = ((MavenModuleSet)item).getPublishersList();
                List<LogstashNotifier> lsList =
                        Util.filter(pList, LogstashNotifier.class);
                if(lsList.isEmpty() != value) {
                    // already matches value.  Do nothing.
                    return false;
                }
                if(value) {
                    pList.add(new LogstashNotifier(-1, false));
                } else {
                    pList.removeAll(lsList);
                }
                return true;
            } else {
                return false;
            }
        }

    }

}
