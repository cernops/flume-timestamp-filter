Name:       flume-timestamp-filtering-interceptor
Version:    0.0
Release:    2%{?dist}
Summary:    Flume interceptor to filter events based on timestamp

Group:      Development/Tools
License:    Apache License, Version 2.0

Source0:    %{name}-%{version}.jar

BuildArch:   noarch
BuildRoot:  %(mktemp -ud %{_tmppath}/%{name}-%{version}-%{release}-XXXXXX)

Requires:   aimon-flume-ng

%description
Flume interceptor to filter events based on timestamp

%install
rm -rf %{buildroot}
install -p -D -m 644 %{SOURCE0} %{buildroot}/usr/lib/flume-ng/plugins.d/%{name}/lib/%{name}-%{version}.jar

%clean
rm -rf %{buildroot}

%files
%defattr(-,root,root,-)
%dir %attr(755,root,root) /usr/lib/flume-ng/plugins.d
%dir %attr(755,root,root) /usr/lib/flume-ng/plugins.d/%{name}
%dir %attr(755,root,root) /usr/lib/flume-ng/plugins.d/%{name}/lib
%attr(644,root,root) /usr/lib/flume-ng/plugins.d/%{name}/lib/%{name}-%{version}.jar

%changelog
* Thu Apr 09 2015 Wataru Takase <wataru.takase@cern.ch> 0.0-2
- Changed Flume target version to 1.5.0 in pom.xml

* Wed Oct 08 2014 Stefano Zilli <stefano.zilli@cern.ch> 0.0-1
- First version
