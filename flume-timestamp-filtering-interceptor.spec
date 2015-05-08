Name:          flume-timestamp-filtering-interceptor
Version:       0.0
Release:       3%{?dist}
Summary:       Flume interceptor to filter events based on timestamp
Source0:       %{name}-%{version}.tar.gz
BuildArch:     noarch

Group:         CERN/Utilities
License:       Apache License, Version 2.0
URL:           https://openstack.cern.ch/

Requires:      aimon-flume-ng


%description
Flume interceptor to filter events based on timestamp

%prep
%setup -q -n %{name}-%{version}

%install
%{__rm} -rf %{buildroot}
install -p -D -m 644 target/%{name}-%{version}.jar %{buildroot}/usr/lib/flume-ng/plugins.d/%{name}/lib/%{name}-%{version}.jar

%clean
%{__rm} -rf %{buildroot}

%files
%defattr(-,root,root,-)

%dir /usr/lib/flume-ng/plugins.d
/usr/lib/flume-ng/plugins.d/*

%changelog
* Thu May 07 2015 Wataru Takase <wataru.takase@cern.ch> 0.0-3
- Changed building way

* Thu Apr 09 2015 Wataru Takase <wataru.takase@cern.ch> 0.0-2
- Changed Flume target version to 1.5.0 in pom.xml

* Wed Oct 08 2014 Stefano Zilli <stefano.zilli@cern.ch> 0.0-1
- First version
